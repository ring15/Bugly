package com.founq.sdk.bugly;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by ring on 2019/5/20.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "a0add45ce9", true);
    }
}
