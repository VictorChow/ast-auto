package me.victor.lombok.core.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;

import me.victor.lombok.core.model.ProcessContext;

/**
 * 抽象执行器
 * @author binbin.hou
 * @since 0.0.2
 */
public abstract class BaseProcessor extends AbstractProcessor {

    /**
     * Messager主要是用来在编译期打log用的
     */
    protected Messager messager;

    /**
     * JavacTrees提供了待处理的抽象语法树
     */
    protected JavacTrees trees;

    /**
     * TreeMaker封装了创建AST节点的一些方法
     */
    protected TreeMaker treeMaker;

    /**
     * Names提供了创建标识符的方法
     */
    protected Names names;

    /**
     * 执行上下文
     */
    protected ProcessContext processContext;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);

        this.processContext = ProcessContext.newInstance().messager(messager)
                .names(names).treeMaker(treeMaker).trees(trees);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public void log(Object... objects) {
        List<String> msg = Arrays.stream(objects).map(Object::toString).collect(Collectors.toList());
        messager.printMessage(Diagnostic.Kind.NOTE, "😏 " + String.join(", ", msg));
    }
}
