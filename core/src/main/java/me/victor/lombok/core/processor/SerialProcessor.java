package me.victor.lombok.core.processor;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.code.Flags;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.tools.Diagnostic;

import me.victor.lombok.core.annotation.Serial;
import me.victor.lombok.core.metadata.LClass;
import me.victor.lombok.core.metadata.LField;
import me.victor.lombok.core.metadata.LObject;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.victor.lombok.core.annotation.Serial")
public class SerialProcessor extends BaseClassProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return Serial.class;
    }

    @Override
    protected void handleClass(LClass lClass) {
        // 给此类添加一个接口
        lClass.addInterface(Serializable.class);
        // 创建一个字段
        createSerialVersionUID(lClass);
    }

    /**
     * private static final long serialVersionUID = 1L;
     *
     * @param lClass 类
     * @since 0.0.1
     */
    private void createSerialVersionUID(LClass lClass) {
        // 获取注解对应的值
        Serial serial = lClass.classSymbol().getAnnotation(Serial.class);

        // 构建对象信息
        LObject value = new LObject(processContext).expression(treeMaker.Literal(serial.value()));
        LField lField = LField.newInstance().modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL)
                .type(Long.class).name("serialVersionUID").value(value);
        lClass.insertField(lField);
    }

}
