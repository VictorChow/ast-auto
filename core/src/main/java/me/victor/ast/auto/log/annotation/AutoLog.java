package me.victor.ast.auto.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by victor on 2022/3/15. (ง •̀_•́)ง
 */

@Retention(RetentionPolicy.SOURCE)
@Target({TYPE, METHOD})
@Documented
public @interface AutoLog {

    String value() default "";
}
