package github.hotstu.tinyritro.lib.anotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;


/**
 * @author hglf
 * @since 2018/5/25
 */
@Retention(CLASS)
@Target(value = TYPE)
public @interface EntryPoint {
    /**
     * entrypoint value, eg. http://192.168.1.1/app
     * @return
     */
    String value() default "";

    /**
     * the name of the generated file,
     * @return
     */
    String name() default "";
}
