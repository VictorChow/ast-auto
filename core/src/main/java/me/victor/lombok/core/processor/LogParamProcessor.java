package me.victor.lombok.core.processor;

import com.cebbank.poin.core.log.CSPSLogger;
import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;

import me.victor.lombok.core.annotation.LogParam;
import me.victor.lombok.core.metadata.LClass;
import me.victor.lombok.core.metadata.LField;
import me.victor.lombok.core.metadata.LObject;

/**
 * Created by victor on 2022/3/15. (ง •̀_•́)ง
 */

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.victor.lombok.core.annotation.LogParam")
public class LogParamProcessor extends BaseClassProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return LogParam.class;
    }

    @Override
    protected void handleClass(LClass lClass) {
        String loggerName = lClass.classDecl().defs.stream()
                .filter(it -> it.getKind() == Tree.Kind.VARIABLE)
                .map(it -> (JCTree.JCVariableDecl) it)
                .filter(it -> it.vartype.toString().contains(CSPSLogger.class.getSimpleName()))
                .findFirst()
                .map(it -> it.name.toString())
                .orElseGet(() -> createPoinLogField(lClass));
        lClass.classDecl().defs.stream()
                .filter(it -> it.getKind() == Tree.Kind.METHOD)
                .map(it -> (JCTree.JCMethodDecl) it)
                .filter(it -> it.sym.getAnnotation(getAnnotationClass()) != null)
                .forEach(it -> insertLogParam(it, loggerName));

        //        LogParam annotation = lClass.classSymbol().getAnnotation(LogParam.class);
    }

    private void insertLogParam(JCTree.JCMethodDecl methodDecl, String loggerName) {
        //        log(methodDecl.name, methodDecl.params);
        java.util.List<JCTree.JCExpression> params = methodDecl.params.stream()
                .map(it -> treeMaker.Binary(JCTree.Tag.PLUS, treeMaker.Literal(it.name + "="), treeMaker.Ident(it)))
                .collect(Collectors.toList());
        java.util.List<JCTree.JCExpression> joinParams = new ArrayList<>();
        joinParams.add(treeMaker.Literal(", "));
        joinParams.addAll(params);
        JCTree.JCMethodInvocation stringJoin = treeMaker.Apply(
                List.nil(),
                chainDots(String.class.getCanonicalName() + ".join"),
                List.from(joinParams)
        );
        JCTree.JCExpression infoParam = treeMaker.Binary(JCTree.Tag.PLUS, treeMaker.Literal(methodDecl.name + "()入参: "), stringJoin);
        JCTree.JCExpressionStatement statement = treeMaker.Exec(treeMaker.Apply(
                List.nil(),
                chainDots(loggerName + ".info"),
                List.of(infoParam)
        ));
        methodDecl.body.stats = methodDecl.body.stats.prependList(List.of(statement));
    }

    private String createPoinLogField(LClass lClass) {
        String loggerName = "logger";
        String pattern = "com.cebbank.poin.core.log.CSPSLogFactory.get";
        String literal = String.format(pattern, lClass.classSymbol().name);
        LObject value = new LObject(processContext).expression(treeMaker.Apply(
                List.nil(),
                chainDots(literal),
                List.of(treeMaker.ClassLiteral(lClass.classSymbol().type))
        ));
        LField lField = LField.newInstance()
                .modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL)
                .type(CSPSLogger.class)
                .name(loggerName)
                .value(value);
        lClass.insertField(lField);
        return loggerName;
    }

    public JCTree.JCExpression chainDots(String components) {
        String[] elems = components.split("\\.");
        JCTree.JCExpression e = null;
        for (String elem : elems) {
            e = e == null ? treeMaker.Ident(names.fromString(elem)) : treeMaker.Select(e, names.fromString(elem));
        }
        return e;
    }

}
