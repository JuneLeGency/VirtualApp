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
                String result = HttpRequest.get("https://www.baidu.com/").body();
                Log.d("baidu", "result" + result);
            }
        });
    }
}
