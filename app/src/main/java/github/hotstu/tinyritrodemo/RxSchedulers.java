package github.hotstu.tinyritrodemo;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxSchedulers {
    public static final FlowableTransformer<?, ?> mTransformer = new FlowableTransformer<Object, Object>() {
        @Override
        public Publisher<Object> apply(Flowable<Object> upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> FlowableTransformer<T, T> io_main() {
        return (FlowableTransformer<T, T>) mTransformer;
    }
}
