package github.hotstu.tinyritrodemo;

import java.util.Map;

import io.github.hotstu.tinyritro.anotations.EntryPoint;
import io.github.hotstu.tinyritro.anotations.PathParam;
import io.github.hotstu.tinyritro.anotations.Query;
import io.github.hotstu.tinyritro.anotations.QueryParam;
import io.reactivex.Flowable;

/**
 * @author hglf
 * @since 2018/5/29
 */
@EntryPoint(value = "http://httpbin.org")
public interface APIService1 {
    /**
     * 请求地址=value + path
     * @param params 请求参数
     * @return
     */
    @Query(path = "/get?id=${id}")
    Flowable<String> get(Map<String, String> params, @PathParam("id") String ids, @PathParam int name);

    /**
     * 请求地址=url
     * @param params 请求参数
     * @return
     */
    @Query(method = Query.Method.POST,url = "http://www.google.com/app/open")
    Flowable<Person> post(Map<String, String> params);

    @Query(method = Query.Method.POST,url = "http://www.google.com/app/open")
    Flowable<Person> post2(@QueryParam String query1);
}
