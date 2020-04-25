package com.wangzhen.plugin.host;

import android.app.Application;
import android.content.res.Resources;

import com.wangzhen.plugin.PluginManager;

/**
 * BaseApplication
 * Created by wangzhen on 2020/4/23.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        AMSHookHelper.hook();
    }

    @Override
    public Resources getResources() {
        Resources resources = PluginManager.getInstance().getPluginResources();
        return resources != null ? resources : super.getResources();
    }
}
