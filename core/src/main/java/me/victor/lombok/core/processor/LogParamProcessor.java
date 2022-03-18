package me.victor.lombok.core.processor;

import com.log.LogFactory;
import com.log.Logger;
import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import me.victor.lombok.core.annotation.LogParam;

/**
 * Created by victor on 2022/3/16. (ง •̀_•́)ง
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("me.victor.lombok.core.annotation.LogParam")
public class LogParamProcessor extends AbstractProcessor {
    private static final String CONSTRUCTOR = "<init>";
    private static final String RETURN_VAL = "$$retVal$$";
    private static final String LOGGER_PREFIX = "<%s.%s> ";

    private final Set<JCTree.JCClassDecl> symbols = new HashSet<>();
    private Messager messager;
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(LogParam.class);
        for (Element element : set) {
            JCTree tree = trees.getTree(element);
            if (element.getKind() == ElementKind.CLASS) {
                handleClass((JCTree.JCClassDecl) tree);
                symbols.add(((JCTree.JCClassDecl) tree));
            } else if (element.getKind() == ElementKind.METHOD) {
                handleMethods((JCTree.JCMethodDecl) tree);
            }
        }
        return false;
    }

    private void handleClass(JCTree.JCClassDecl classDecl) {
        String loggerName = getLoggerName(classDecl);
        classDecl.defs.stream()
                .filter(it -> it.getKind() == Tree.Kind.METHOD)
                .map(it -> (JCTree.JCMethodDecl) it)
                .filter(it -> !it.name.toString().contains(CONSTRUCTOR))
                .forEach(it -> insertLogLogic(classDecl, it, loggerName));
    }

    private void handleMethods(JCTree.JCMethodDecl methodDecl) {
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(methodDecl.sym.owner);
        if (symbols.contains(classDecl)) {
            //标记在类上的注解已经处理所以方法, 无需在标记方法上再次处理
            return;
        }
        insertLogLogic(classDecl, methodDecl, getLoggerName(classDecl));
    }

    private String getLoggerName(JCTree.JCClassDecl classDecl) {
        Predicate<JCTree.JCVariableDecl> matchFieldPredicate = it -> {
            if (it.vartype instanceof JCTree.JCIdent) {
                return Objects.equals(it.vartype.type.toString(), Logger.class.getCanonicalName());
            } else if (it.vartype instanceof JCTree.JCFieldAccess) {
                return Objects.equals(it.vartype.toString(), Logger.class.getCanonicalName());
            }
            return false;
        };
        return classDecl.defs.stream()
                .filter(it -> it.getKind() == Tree.Kind.VARIABLE)
                .map(it -> (JCTree.JCVariableDecl) it)
                .filter(matchFieldPredicate)
                .findFirst()
                .map(it -> it.name.toString())
                .orElseGet(() -> createLogField(classDecl));
    }

    private String createLogField(JCTree.JCClassDecl classDecl) {
        String defaultLoggerName = "logger";
        String statement = LogFactory.class.getCanonicalName() + ".get";
        JCTree.JCMethodInvocation loggerInit = treeMaker.Apply(
                List.nil(),
                chainDots(statement),
                List.of(treeMaker.ClassLiteral(classDecl.sym.type))
        );
        JCTree.JCVariableDecl loggerDef = treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL),
                names.fromString(defaultLoggerName),
                chainDots(Logger.class.getCanonicalName()),
                loggerInit
        );
        classDecl.defs = classDecl.defs.prepend(loggerDef);
        return defaultLoggerName;
    }

    private void insertLogLogic(JCTree.JCClassDecl classDecl, JCTree.JCMethodDecl methodDecl, String loggerName) {
        String prefix = String.format(LOGGER_PREFIX, classDecl.name, methodDecl.name);
        handleReturnLogic(methodDecl, loggerName, prefix);
        handleParamLogic(methodDecl, loggerName, prefix);
    }

    private void handleParamLogic(JCTree.JCMethodDecl methodDecl, String loggerName, String loggerPrefix) {
        if (methodDecl.params.isEmpty()) {
            //方法无入参, 无需处理入参打印
            return;
        }
        java.util.List<JCTree.JCExpression> params = methodDecl.params.stream()
                .map(it -> treeMaker.Binary(
                        JCTree.Tag.PLUS,
                        treeMaker.Literal(it.name + "="),
                        handleParamToString(it))
                )
                .collect(Collectors.toList());
        java.util.List<JCTree.JCExpression> joinParams = new ArrayList<>();
        joinParams.add(treeMaker.Literal(", "));
        joinParams.addAll(params);
        JCTree.JCMethodInvocation stringJoin = treeMaker.Apply(
                List.nil(),
                chainDots(String.class.getCanonicalName() + ".join"),
                List.from(joinParams)
        );
        JCTree.JCExpression infoParam = treeMaker.Binary(
                JCTree.Tag.PLUS,
                treeMaker.Literal(loggerPrefix + "args: "),
                stringJoin
        );
        JCTree.JCExpressionStatement statement = treeMaker.Exec(treeMaker.Apply(
                List.nil(),
                chainDots(loggerName + ".info"),
                List.of(infoParam)
        ));
        methodDecl.body.stats = methodDecl.body.stats.prepend(statement);
    }

    private void handleReturnLogic(JCTree.JCMethodDecl methodDecl, String loggerName, String loggerPrefix) {
        if (methodDecl.restype.type instanceof Type.JCVoidType) {
            //方法返回类型void, 无需处理返回值打印
            return;
        }
        JCTree.JCVariableDecl retValDef = treeMaker.VarDef(
                treeMaker.Modifiers(0),
                names.fromString(RETURN_VAL),
                chainDots(methodDecl.restype.type.toString()),
                treeMaker.Literal(TypeTag.BOT, null)
        );
        methodDecl.body.stats = methodDecl.body.stats.prepend(retValDef);
        methodDecl.accept(new TreeTranslator() {
            @Override
            public void visitBlock(JCTree.JCBlock jcBlock) {
                super.visitBlock(jcBlock);
                List<JCTree.JCStatement> blockStats = jcBlock.stats;
                if (blockStats == null || blockStats.isEmpty()) {
                    return;
                }
                ListBuffer<JCTree.JCStatement> buffer = new ListBuffer<>();
                for (JCTree.JCStatement stat : blockStats) {
                    if (stat instanceof JCTree.JCReturn) {
                        JCTree.JCReturn jcReturn = (JCTree.JCReturn) stat;
                        JCTree.JCExpressionStatement assign = treeMaker.Exec(treeMaker.Assign(
                                treeMaker.Ident(names.fromString(RETURN_VAL)),
                                jcReturn.getExpression()
                        ));
                        JCTree.JCExpressionStatement logExec = treeMaker.Exec(treeMaker.Apply(
                                List.nil(),
                                chainDots(loggerName + ".info"),
                                List.of(treeMaker.Binary(
                                        JCTree.Tag.PLUS,
                                        treeMaker.Literal(loggerPrefix + "return: "),
                                        treeMaker.Ident(names.fromString(RETURN_VAL))
                                ))
                        ));
                        JCTree.JCReturn returnStat = treeMaker.Return(treeMaker.Ident(names.fromString(RETURN_VAL)));
                        buffer.appendList(List.of(assign, logExec, returnStat));
                    } else {
                        buffer.append(stat);
                    }
                }
                jcBlock.stats = buffer.toList();
            }

            @Override
            public void visitIf(JCTree.JCIf jcIf) {
                if (jcIf.thenpart != null && !(jcIf.thenpart instanceof JCTree.JCBlock)) {
                    jcIf.thenpart = treeMaker.Block(0, List.of(jcIf.thenpart));
                }
                if (jcIf.elsepart != null && !(jcIf.elsepart instanceof JCTree.JCBlock)) {
                    jcIf.elsepart = treeMaker.Block(0, List.of(jcIf.elsepart));
                }
                super.visitIf(jcIf);
            }

            @Override
            public void visitCase(JCTree.JCCase jcCase) {
                List<JCTree.JCStatement> stats = jcCase.stats;
                if (stats != null && !stats.isEmpty() && !(stats.get(0) instanceof JCTree.JCBlock)) {
                    jcCase.stats = List.of(treeMaker.Block(0, stats));
                }
                super.visitCase(jcCase);
            }
        });
    }

    private JCTree.JCExpression handleParamToString(JCTree.JCVariableDecl it) {
        if (it.sym.type instanceof Type.ArrayType) {
            return treeMaker.Apply(
                    List.nil(),
                    chainDots(Arrays.class.getCanonicalName() + ".toString"),
                    List.of(treeMaker.Ident(it))
            );
        }
        return treeMaker.Ident(it);
    }

    private JCTree.JCExpression chainDots(String components) {
        String[] elems = components.split("\\.");
        JCTree.JCExpression e = null;
        for (String elem : elems) {
            e = e == null ? treeMaker.Ident(names.fromString(elem)) : treeMaker.Select(e, names.fromString(elem));
        }
        return e;
    }

    private void log(Object... objects) {
        messager.printMessage(Diagnostic.Kind.NOTE, "😏😏😏\n" + Arrays.stream(objects).map(Object::toString).collect(Collectors.joining(", ")));
    }
}
