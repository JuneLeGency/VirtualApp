package june.legency.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import com.lc.puppet.storage.IObComment;
import com.zzhoujay.richtext.RichText;

import june.legency.env.R;


public class DetailActivity extends AppCompatActivity {

    private TextView md;
    private String key;
    private String value;
    private IObComment.Comment comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial);
        md = (TextView) findViewById(R.id.md_text);

        Bundle e = getIntent().getExtras();
        key = (String) e.get("key");
        value = (String) e.get("value");
        IObComment.get(this);
        comment = IObComment.get(key);
        show();
    }

    private static String test_str = "# MarkDown\n" +
            "\n" +
            "> Android平台的Markdown解析器\n" +
            "\n" +
            "##### 开发中。。。\n" +
            "\n" +
            "__by zzhoujay__\n";

    private void show() {
        String s = comment.toMarkDown(value);
        if (TextUtils.isEmpty(s))
            s = test_str + "```json\n" + value + "\n```";
        RichText.fromMarkdown(s).into(md);
    }
}
