package legency.june.networktest;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return super.getPasswordAuthentication();
            }
        });
        setContentView(R.layout.activity_main);
        ThreadUtil.get().execute(new Runnable() {
            @Override
            public void run() {
                String s = HttpRequest.get("https://www.baidu.com").body();
                Log.d("asdasd", s);
            }
        });
    }
}
