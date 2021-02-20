package com.founq.sdk.bugly;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

/**
 * Created by ring on 2019/5/20.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashReport.initCrashReport(getApplicationContext(), "a0add45ce9", true);
        Bugly.init(getApplicationContext(), "a0add45ce9", true);

        //sd卡需要低版本权限，target高版本获取不到，从服务器获取时，直接存到私有目录就行，或者，禁用分区存储
        HotFix.installPatch(this, new File("/sdcard/patch.jar"));
    }
}
