package github.hotstu.tinyritrodemo.aop;

/**
 * @author hglf
 * @since 2018/6/4
 */
public class DefaultDenyHandler implements DenyHandler {
    @Override
    public void onDenied(Object ac, Object permisson, String Tag) {
        System.out.println("denied");
    }
}
