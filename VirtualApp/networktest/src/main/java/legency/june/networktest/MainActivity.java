package legency.june.networktest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreadUtil.get().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = HttpRequest.get("http://httpbin.org/ip").body();
                    Log.d("legency", "httpbin.org" + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
