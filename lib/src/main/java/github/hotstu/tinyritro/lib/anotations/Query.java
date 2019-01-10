package github.hotstu.tinyritro.lib.anotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;


/**
 * @author hglf
 * @since 2018/5/25
 */
@Retention(CLASS)
@Target(value = METHOD)
public @interface Query {
    /**
     * path will combine with entrypoint's value then produce request url
     * @return
     */
    String path() default "";

    /**
     * url has higher priority than path, which will override hte produced request url,
     * @return
     */
    String url() default "";

    /**
     * get or post
     * @return
     */
    Method method() default Method.GET;
    enum Method {
        GET("get"),
        POST("post");

        String value;
        Method(String post) {
            this.value = post;
        }

        public String getValue() {
            return value;
        }
    }

}
