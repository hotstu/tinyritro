package io.github.hotstu.tinyritro.anotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * @author hglf
 * @since 2018/5/25
 */
@Retention(CLASS)
@Target(value = PARAMETER)
public @interface PathParam {
    String value() default "";
}
