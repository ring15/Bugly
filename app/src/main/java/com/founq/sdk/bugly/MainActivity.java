package com.founq.sdk.bugly;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.tv_show);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //测试
            case R.id.btn_test:
                CrashReport.testJavaCrash();
                break;
            case R.id.btn_test1:
                CrashReport.testANRCrash();
                break;
            case R.id.btn_test2:
                nullpoint();
                break;
            case R.id.btn_test3:
                longtime();
                break;
        }
    }

    private void longtime() {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText("ok");
            }
        });
    }

    private void nullpoint() {
        String s = null;
        Log.i("s", s);
    }


}
