package github.hotstu.tinyritrodemo;

import java.util.Map;

import github.hotstu.tinyritro.anotations.EntryPoint;
import github.hotstu.tinyritro.anotations.Query;
import io.reactivex.Flowable;

/**
 * @author hglf
 * @since 2018/5/29
 */
@EntryPoint(value = "http://httpbin.org/get")
public interface MyClass {
    /**
     * 请求地址=value + path
     * @param params 请求参数
     * @return
     */
    @Query(path = "")
    Flowable<String> get(Map<String, String> params);

    /**
     * 请求地址=url
     * @param params 请求参数
     * @return
     */
    @Query(method = Query.Method.POST,url = "http://www.google.com/app/open")
    Flowable<Person> post(Map<String, String> params);
}
