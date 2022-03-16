package me.victor.lombok.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface Serial {

    /**
     * 每次固定生成默认值
     * 序列号建议直接使用固定的默认值，除非有特殊需求。
     * @return 默认为-1L
     */
    long value() default 1L;

}
