package github.hotstu.tinyritrodemo.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WithinPermission {
    String[] value();
    String tag() default "";
    Class handler() default DefaultDenyHandler.class;
}
