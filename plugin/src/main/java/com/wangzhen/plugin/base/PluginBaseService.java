package com.wangzhen.plugin.base;

import android.app.Service;
import android.content.Intent;
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
}
