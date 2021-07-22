package github.hotstu.tinyritrodemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

import io.github.hotstu.tinyritro.TinyRitro;
import io.reactivex.disposables.Disposable;


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
        Toast.makeText(this, "watch logcat", Toast.LENGTH_LONG).show();

        HashMap<String, String> params = new HashMap<>();
        params.put("aaa", "bbb");
        Disposable subscribe = build.getAPIService1().get(params, "233", 233)
                .compose(RxSchedulers.io_main())
                .subscribe(strings -> Log.d("result", "" + strings), throwable -> throwable.printStackTrace());
    }


}
