package me.victor.ast.auto.log.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER})
@Documented
public @interface LogJSON {
}
