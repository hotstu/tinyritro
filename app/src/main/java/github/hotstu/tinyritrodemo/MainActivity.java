package github.hotstu.tinyritrodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

import github.hotstu.tinyritro.gen.TinyRitro;
import github.hotstu.tinyritrodemo.aop.WithinPermission;
import io.reactivex.functions.Consumer;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity {

    private TinyRitro build;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        build = new TinyRitro.Builder().client(OkHttpClientMgr.getInstance())
                .build();
    }

    public void send(View view) {
        HashMap<String, String> params = new HashMap<>();
        params.put("aaa", "bbb");
        build.getAPIService1().get(params, "233",233)
                .compose(RxSchedulers.<String>io_main())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String strings) throws Exception {
                        Log.d("result", "" + strings);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    @WithinPermission({CAMERA})
    public void send2(View v) {
        Toast.makeText(this, "open camera", Toast.LENGTH_LONG).show();
    }
}
