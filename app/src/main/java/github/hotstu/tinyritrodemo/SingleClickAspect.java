package github.hotstu.tinyritrodemo;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import androidx.fragment.app.Fragment;
import github.hotstu.tinyritrodemo.aop.DenyHandler;
import github.hotstu.tinyritrodemo.aop.WithinPermission;
import io.reactivex.functions.Consumer;

/**
 * 防止View被连续点击,间隔时间600ms
 */
@Aspect
public class SingleClickAspect {

    @Pointcut(" execution(@github.hotstu.tinyritrodemo.aop.WithinPermission * *(..))  && @annotation(permission) && args(..,v)")
//方法切入点
    public void methodAnnotated(WithinPermission permission, View v) {
    }

    @Around("methodAnnotated(permission,v)")//在连接点进行方法替换
    public void aroundJoinPoint(final ProceedingJoinPoint joinPoint, final WithinPermission permission, View v) throws Throwable {
        System.out.println("aroundJoinPoint");

        final Object aThis = joinPoint.getThis();
        final Activity ac;
        if (aThis instanceof Fragment) {
            ac = ((Fragment) aThis).getActivity();
        } else if (aThis instanceof Activity) {
            ac = (Activity) aThis;
        } else {
            throw new IllegalStateException("method must belong to activity or fragment");
        }
        RxPermissions rxPermissions = new RxPermissions(ac);
        Log.e(aThis.getClass().getCanonicalName(), "before proceed " + permission.value() + v.getId());

        rxPermissions
                .requestEachCombined(permission.value())
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission granted) throws Exception {
                        if(granted.granted) {
                            System.out.println("granted");
                            try {
                                joinPoint.proceed();//执行原方法
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } else {
                            if(aThis instanceof DenyHandler) {
                                ((DenyHandler) aThis).onDenied(ac, granted, permission.tag());
                            } else {
                                DenyHandler handler = (DenyHandler) permission.handler().newInstance();
                                handler.onDenied(ac, granted,permission.tag());
                            }

                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

}
