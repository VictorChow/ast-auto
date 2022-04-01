package me.victor.ast.auto.stringfog.processor;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
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
import java.util.Set;
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

import me.victor.ast.auto.stringfog.XOR;
import me.victor.ast.auto.stringfog.annotation.StringFog;

/**
 * Created by victor on 2022/3/30. (ง •̀_•́)ง
 */
@AutoService(Processor.class)
public class StringFogProcessor extends AbstractProcessor {

    private Messager messager;
    private JavacTrees trees;
    private TreeMaker maker;
    private Names names;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(StringFog.class.getCanonicalName());
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
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(StringFog.class);
        for (Element element : set) {
            JCTree tree = trees.getTree(element);
            if (element.getKind() == ElementKind.CLASS) {
                tree.accept(new TreeTranslator() {

                    @Override
                    public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
                        super.visitVarDef(jcVariableDecl);
                        jcVariableDecl.init = handleJCLiteral(jcVariableDecl.init);
                    }

                    @Override
                    public void visitBinary(JCTree.JCBinary jcBinary) {
                        super.visitBinary(jcBinary);
                        jcBinary.lhs = handleJCLiteral(jcBinary.lhs);
                        jcBinary.rhs = handleJCLiteral(jcBinary.rhs);
                    }

                    @Override
                    public void visitAssign(JCTree.JCAssign jcAssign) {
                        super.visitAssign(jcAssign);
                        jcAssign.rhs = handleJCLiteral(jcAssign.rhs);
                    }

                    @Override
                    public void visitApply(JCTree.JCMethodInvocation jcMethodInvocation) {
                        super.visitApply(jcMethodInvocation);
                        ListBuffer<JCTree.JCExpression> buffer = new ListBuffer<>();
                        for (JCTree.JCExpression arg : jcMethodInvocation.args) {
                            if (arg instanceof JCTree.JCLiteral && ((JCTree.JCLiteral) arg).typetag == TypeTag.CLASS) {
                                buffer.add(handleJCLiteral(arg));
                            } else {
                                buffer.add(arg);
                            }
                        }
                        jcMethodInvocation.args = buffer.toList();
                    }
                });
            }
        }
        return false;
    }

    private JCTree.JCExpression handleJCLiteral(JCTree.JCExpression jcExpression) {
        if (jcExpression instanceof JCTree.JCLiteral && ((JCTree.JCLiteral) jcExpression).typetag == TypeTag.CLASS) {
            JCTree.JCLiteral jcLiteral = (JCTree.JCLiteral) jcExpression;
            String encode = XOR.coding(jcLiteral.value.toString());
            return maker.Apply(
                    List.nil(),
                    chainDots(XOR.class.getCanonicalName() + ".coding"),
                    List.of(maker.Literal(encode))
            );
        }
        return jcExpression;
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
