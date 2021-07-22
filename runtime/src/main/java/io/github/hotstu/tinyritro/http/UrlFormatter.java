package io.github.hotstu.tinyritro.http;

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

/**
 * @author hglf
 * @since 2018/6/25
 */
public class UrlFormatter {
    public static String format(String url, Map<String, String> replcement) {
        return StringSubstitutor.replace(url, replcement);
    }

}
