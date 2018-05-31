package github.hotstu.tinyritrodemo;


import java.util.Map;

import github.hotstu.tinyritro.anotations.EntryPoint;
import github.hotstu.tinyritro.anotations.Query;
import io.reactivex.Flowable;

/**
 * @author hglf
 * @since 2018/5/29
 */
@EntryPoint(value = "http://httpbin.org/get", name = "CustomeClassName")
public interface AnotherClass {
    @Query(path = "/app/login")
    Flowable<Person> best(Map<String, String> params);
}
