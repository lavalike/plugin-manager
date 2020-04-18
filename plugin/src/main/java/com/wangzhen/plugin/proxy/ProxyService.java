package com.wangzhen.plugin.proxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.wangzhen.plugin.hook.ServiceManager;

/**
 * service proxy
 * Created by wangzhen on 2020/4/9.
 */
public class ProxyService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ServiceManager.getInstance().onStartCommand(intent, flags, startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
