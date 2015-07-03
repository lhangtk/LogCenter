package com.iflytek.logcenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.iflytek.logcenter.extract.Writer;
import com.iflytek.logcenter.extract.utils.IdGenerator;

public class MyActivity extends Activity {
    private Button btnWrite;
    private Button btnUrlTest;
    private Writer writer;
    private String sessionId;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        writer = new Writer(MyActivity.this);
        btnUrlTest = (Button) findViewById(R.id.btn_url);
        btnWrite = (Button) findViewById(R.id.btn_write);
        btnUrlTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writer.trigger(null,"testClicked","9998");
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writer.trigger("2300000015000004747","9999");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        writer.quit("2300000015000004747", sessionId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionId = IdGenerator.generate();
        writer.entry("2300000015000004747", sessionId);
    }
}
