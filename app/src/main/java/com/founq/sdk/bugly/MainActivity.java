package com.founq.sdk.bugly;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.tv_show);
        request();
    }

    private void request() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0x01);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //测试
            case R.id.btn_test:
//                CrashReport.testJavaCrash();
                break;
            case R.id.btn_test1:
//                CrashReport.testANRCrash();
                mTextView.setText("success");
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
