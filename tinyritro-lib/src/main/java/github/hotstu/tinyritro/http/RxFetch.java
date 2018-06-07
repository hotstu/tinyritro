package github.hotstu.tinyritro.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RxFetch {

    private  final Gson g = new GsonBuilder().create();
    private  final OkHttpClient client;

    public RxFetch(OkHttpClient client) {
        this.client = client;
    }

    private <T> Flowable<T> request(final Request request, final Type clazz) {
        return Flowable.fromCallable(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return client.newCall(request).execute();
            }
        }).map(new Function<Response, String>() {
            @Override
            public String apply(Response response) throws Exception {
                String result;
                try {
                    result = response.body().string();
                } finally {
                    response.body().close();
                }
                if (response.code() < 300) {
                    return result;
                } else {
                    throw new HTTPException(response.code(), result);
                }

            }
        }).map(new Function<String, T>() {
            @Override
            public T apply(String s) throws Exception {
                Class<String> stringClass = String.class;
                if (stringClass.equals(clazz)) {
                    return (T)s;
                }
                return g.fromJson(s, clazz);
            }
        });
    }


    public static class HTTPException extends IOException {
        public int code;
        public String result;
        HTTPException(int code, String result) {
            super();
            this.code = code;
            this.result = result;
        }
    }


    public <T> Flowable<T> get(String url, Map<String, String> params, final Type clazz) {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                if(param.getKey() == null ) {
                    throw new IllegalArgumentException("key == null");
                }
                if (param.getValue() == null) {
                    throw new IllegalArgumentException("value == null");
                }
                builder.addQueryParameter(param.getKey(),param.getValue());
            }
        }
        Request req = new Request.Builder()
                .url(builder.build())
                .build();
        return request(req, clazz);
    }

    public <T> Flowable<T> post(String url, Map<String, String> params, final Type clazz) {
        FormBody.Builder form = new FormBody.Builder();

        if (params != null) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                if(param.getKey() == null ) {
                    throw new IllegalArgumentException("key == null");
                }
                if (param.getValue() == null) {
                    throw new IllegalArgumentException("value == null");
                }
                form.add(param.getKey(), param.getValue());
            }
        }
        Request req = new Request.Builder()
                .url(url)
                .post(form.build())
                .build();
        return request(req, clazz);
    }

}
