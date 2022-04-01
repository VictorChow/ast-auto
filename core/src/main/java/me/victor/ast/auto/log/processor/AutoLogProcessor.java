package me.victor.ast.auto.log.processor;

import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import me.victor.ast.auto.log.Arg;
import me.victor.ast.auto.log.annotation.AutoLog;
import me.victor.ast.auto.log.logger.AutoLogAdapter;

/**
 * Created by victor on 2022/3/16. (ง •̀_•́)ง
 */
@AutoService(Processor.class)
public class AutoLogProcessor extends AbstractProcessor {
    private static final String CONSTRUCTOR = "<init>";
    private static final String RETURN_VAL = "$$ret$$";
    private static final String INVOKE_TIME = "$$time$$";
    private static final String LOGGER_PREFIX = "<%s.%s> ";

    private final Set<JCTree.JCClassDecl> symbols = new HashSet<>();
    private final Map<String, Supplier<JCTree.JCExpression>> retInitVal = new HashMap<>();
    private final Map<String, TypeTag> retType = new HashMap<>();

    private Messager messager;
    private JavacTrees trees;
    private TreeMaker maker;
    private Names names;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AutoLog.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        maker = TreeMaker.instance(context);
        names = Names.instance(context);
        initPrimitiveDefValues();
    }

    private void initPrimitiveDefValues() {
        retInitVal.put("byte", () -> maker.TypeCast(maker.TypeIdent(TypeTag.BYTE), maker.Literal(0)));
        retInitVal.put("short", () -> maker.TypeCast(maker.TypeIdent(TypeTag.SHORT), maker.Literal(0)));
        retInitVal.put("int", () -> maker.Literal(0));
        retInitVal.put("long", () -> maker.Literal(0));
        retInitVal.put("double", () -> maker.Literal(0));
        retInitVal.put("float", () -> maker.Literal(0));
        retInitVal.put("boolean", () -> maker.Literal(false));
        retInitVal.put("char", () -> maker.TypeCast(maker.TypeIdent(TypeTag.CHAR), maker.Literal(0)));

        retType.put("byte", TypeTag.BYTE);
        retType.put("short", TypeTag.SHORT);
        retType.put("int", TypeTag.INT);
        retType.put("long", TypeTag.LONG);
        retType.put("double", TypeTag.DOUBLE);
        retType.put("float", TypeTag.FLOAT);
        retType.put("boolean", TypeTag.BOOLEAN);
        retType.put("char", TypeTag.CHAR);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(AutoLog.class);
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
        classDecl.defs.stream()
                .filter(it -> it.getKind() == Tree.Kind.METHOD)
                .map(it -> (JCTree.JCMethodDecl) it)
                .filter(it -> !it.name.toString().contains(CONSTRUCTOR))
                .forEach(it -> insertLogLogic(classDecl, it));
    }

    private void handleMethods(JCTree.JCMethodDecl methodDecl) {
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(methodDecl.sym.owner);
        if (symbols.contains(classDecl)) {
            //标记在类上的注解已经处理所有方法, 无需在标记方法上再次处理
            return;
        }
        insertLogLogic(classDecl, methodDecl);
    }

    private void insertLogLogic(JCTree.JCClassDecl classDecl, JCTree.JCMethodDecl methodDecl) {
        String logPrefix = String.format(LOGGER_PREFIX, classDecl.name, methodDecl.name);
        wrapSingleStatementToBlock(methodDecl);
        //先包装try...finally再定义time变量, 不然time变量被包在try中导致finally里获取不到time变量
        handleTimeLogic(methodDecl, logPrefix);
        handleReturnLogic(methodDecl);
        //处理参数的放在最后保证时间戳获取在方法的第一行, 时间比较精确
        handleArgsLogic(methodDecl, logPrefix);
    }

    private void wrapSingleStatementToBlock(JCTree.JCMethodDecl methodDecl) {
        methodDecl.accept(new TreeTranslator() {
            @Override
            public void visitIf(JCTree.JCIf jcIf) {
                if (jcIf.thenpart != null && !(jcIf.thenpart instanceof JCTree.JCBlock)) {
                    jcIf.thenpart = maker.Block(0, List.of(jcIf.thenpart));
                }
                if (jcIf.elsepart != null && !(jcIf.elsepart instanceof JCTree.JCBlock)) {
                    jcIf.elsepart = maker.Block(0, List.of(jcIf.elsepart));
                }
                super.visitIf(jcIf);
            }

            @Override
            public void visitCase(JCTree.JCCase jcCase) {
                List<JCTree.JCStatement> stats = jcCase.stats;
                if (stats != null && !stats.isEmpty() && !(stats.get(0) instanceof JCTree.JCBlock)) {
                    jcCase.stats = List.of(maker.Block(0, stats));
                }
                super.visitCase(jcCase);
            }
        });
    }

    private void handleArgsLogic(JCTree.JCMethodDecl methodDecl, String logPrefix) {
        JCTree.JCVariableDecl timeVarDef = maker.VarDef(
                maker.Modifiers(0),
                names.fromString(INVOKE_TIME),
                maker.TypeIdent(TypeTag.LONG),
                maker.Apply(List.nil(),
                        chainDots(System.class.getCanonicalName() + ".currentTimeMillis"),
                        List.nil()));
        if (methodDecl.params.isEmpty()) {
            //方法无入参, 无需处理入参打印, 只添加time字段
            methodDecl.body.stats = methodDecl.body.stats.prepend(timeVarDef);
            return;
        }
        java.util.List<JCTree.JCExpression> args = methodDecl.params.stream()
                .map(it -> maker.NewClass(
                        null,
                        List.nil(),
                        chainDots(Arg.class.getCanonicalName()),
                        List.of(maker.Literal(it.name.toString()), maker.Ident(it)),
                        null
                ))
                .collect(Collectors.toList());
        List<JCTree.JCExpression> param = List.from(args).prepend(maker.Literal(logPrefix + "args: "));
        JCTree.JCExpressionStatement logArgStat = maker.Exec(maker.Apply(
                List.nil(),
                chainDots(AutoLogAdapter.class.getCanonicalName() + ".logArgs"),
                param
        ));
        methodDecl.body.stats = methodDecl.body.stats.prependList(List.of(timeVarDef, logArgStat));
    }

    private void handleReturnLogic(JCTree.JCMethodDecl methodDecl) {
        if (methodDecl.restype.type instanceof Type.JCVoidType) {
            //方法返回类型void, 不处理return打印
            return;
        }
        //去除泛型<...>
        String returnType = methodDecl.restype.type.toString().replaceAll("<.*>", "");
        JCTree.JCExpression retTypeExp;
        JCTree.JCExpression retValueExp;
        if (retInitVal.containsKey(returnType)) {
            retTypeExp = maker.TypeIdent(retType.get(returnType));
            retValueExp = retInitVal.get(returnType).get();
        } else {
            retTypeExp = chainDots(returnType);
            retValueExp = maker.Literal(TypeTag.BOT, null);
        }
        JCTree.JCVariableDecl retValDef = maker.VarDef(
                maker.Modifiers(0),
                names.fromString(RETURN_VAL),
                retTypeExp,
                retValueExp
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
                        JCTree.JCExpressionStatement assign = maker.Exec(maker.Assign(
                                maker.Ident(names.fromString(RETURN_VAL)),
                                jcReturn.getExpression()
                        ));
                        JCTree.JCReturn returnStat = maker.Return(maker.Ident(names.fromString(RETURN_VAL)));
                        buffer.appendList(List.of(assign, returnStat));
                    } else {
                        buffer.append(stat);
                    }
                }
                jcBlock.stats = buffer.toList();
            }
        });
    }

    private void handleTimeLogic(JCTree.JCMethodDecl methodDecl, String logPrefix) {
        List<JCTree.JCStatement> stats = methodDecl.restype.type instanceof Type.JCVoidType
                ? List.of(makeLogTimeStatement(logPrefix))
                : List.of(makeLogReturnStatement(logPrefix), makeLogTimeStatement(logPrefix));
        methodDecl.body = maker.Block(0,
                List.of(maker.Try(
                        methodDecl.body,
                        List.nil(),
                        maker.Block(0, stats)
                )));
    }

    private JCTree.JCExpressionStatement makeLogReturnStatement(String logPrefix) {
        return maker.Exec(maker.Apply(
                List.nil(),
                chainDots(AutoLogAdapter.class.getCanonicalName() + ".logReturn"),
                List.of(maker.Literal(logPrefix + "return: "),
                        maker.Ident(names.fromString(RETURN_VAL)))
        ));
    }

    private JCTree.JCExpressionStatement makeLogTimeStatement(String logPrefix) {
        JCTree.JCExpression timeExp = maker.Binary(
                JCTree.Tag.MINUS,
                maker.Apply(
                        List.nil(),
                        chainDots(System.class.getCanonicalName() + ".currentTimeMillis"),
                        List.nil()),
                maker.Ident(names.fromString(INVOKE_TIME))
        );
        return maker.Exec(maker.Apply(
                List.nil(),
                chainDots(AutoLogAdapter.class.getCanonicalName() + ".logTime"),
                List.of(maker.Literal(logPrefix + "time: "), timeExp)
        ));
    }

    private JCTree.JCExpression chainDots(String components) {
        String[] elems = components.split("\\.");
        JCTree.JCExpression e = null;
        for (String elem : elems) {
            e = e == null ? maker.Ident(names.fromString(elem)) : maker.Select(e, names.fromString(elem));
        }
        return e;
    }

    private void log(Object... objects) {
        messager.printMessage(Diagnostic.Kind.NOTE, "😏😏😏\n" + Arrays.stream(objects).map(Object::toString).collect(Collectors.joining(", ")));
    }
}
