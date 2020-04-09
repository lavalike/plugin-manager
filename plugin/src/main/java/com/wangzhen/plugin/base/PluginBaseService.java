package com.wangzhen.plugin.base;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.wangzhen.plugin.callback.PluginServiceLifecycle;

/**
 * all services in plugin must extend this
 * Created by wangzhen on 2020/4/9.
 */
public class PluginBaseService extends Service implements PluginServiceLifecycle {
    private Service mProxy;

    @Override
    public void attach(Service service) {
        mProxy = service;
    }

    public Service getService() {
        return mProxy == null ? this : mProxy;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public String getPackageName() {
        if (mProxy != null) {
            return mProxy.getPackageName();
        }
        return super.getPackageName();
    }

    @Override
    public PackageManager getPackageManager() {
        if (mProxy != null) {
            return mProxy.getPackageManager();
        }
        return super.getPackageManager();
    }

    @Override
    public boolean stopService(Intent name) {
        if (mProxy != null) {
            return mProxy.stopService(name);
        }
        return super.stopService(name);
    }
}
