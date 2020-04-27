package com.wangzhen.plugin.host;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * BaseApplication
 * Created by wangzhen on 2020/4/23.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CrashReport.initCrashReport(this, "fdeed497da", true);
    }
}
