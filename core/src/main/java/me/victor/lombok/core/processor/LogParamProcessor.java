package me.victor.lombok.core.processor;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
                .forEach(it -> insertLogParam(classDecl, it, loggerName));
    }

    private void handleMethods(JCTree.JCMethodDecl methodDecl) {
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(methodDecl.sym.owner);
        if (symbols.contains(classDecl)) {
            //标记在类上的注解已经处理所以方法, 无需在标记方法上再次处理
            return;
        }
        insertLogParam(classDecl, methodDecl, getLoggerName(classDecl));
    }


    private String getLoggerName(JCTree.JCClassDecl classDecl) {
        return classDecl.defs.stream()
                .filter(it -> it.getKind() == Tree.Kind.VARIABLE)
                .map(it -> (JCTree.JCVariableDecl) it)
                .filter(it -> it.vartype.toString().contains(CSPSLogger.class.getSimpleName()))
                .findFirst()
                .map(it -> it.name.toString())
                .orElseGet(() -> createPoinLogField(classDecl));
    }

    private String createPoinLogField(JCTree.JCClassDecl classDecl) {
        String defaultLoggerName = "logger";
        String statement = CSPSLogFactory.class.getCanonicalName() + ".get";
        JCTree.JCMethodInvocation loggerInit = treeMaker.Apply(
                List.nil(),
                chainDots(statement),
                List.of(treeMaker.ClassLiteral(classDecl.sym.type))
        );
        JCTree.JCVariableDecl loggerDef = treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL),
                names.fromString(defaultLoggerName),
                chainDots(CSPSLogger.class.getCanonicalName()),
                loggerInit
        );
        classDecl.defs = classDecl.defs.prepend(loggerDef);
        return defaultLoggerName;
    }

    private void insertLogParam(JCTree.JCClassDecl classDecl, JCTree.JCMethodDecl methodDecl, String loggerName) {
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
                treeMaker.Literal("<" + classDecl.name + "." + methodDecl.name + "> args: "),
                stringJoin
        );
        JCTree.JCExpressionStatement statement = treeMaker.Exec(treeMaker.Apply(
                List.nil(),
                chainDots(loggerName + ".info"),
                List.of(infoParam)
        ));
        methodDecl.body.stats = methodDecl.body.stats.prependList(List.of(statement));
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
        messager.printMessage(Diagnostic.Kind.NOTE, "😏 " + Arrays.stream(objects).map(Object::toString).collect(Collectors.joining(", ")));
    }
}
