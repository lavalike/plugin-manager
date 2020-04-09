package com.wangzhen.plugin.callback;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * plugin service lifecycle callbacks
 * Created by wangzhen on 2020/4/9.
 */
public interface PluginServiceLifecycle {
    void attach(Service service);

    void onCreate();

    int onStartCommand(Intent intent, int flags, int startId);

    void onDestroy();

    void onConfigurationChanged(Configuration newConfig);

    void onLowMemory();

    void onTrimMemory(int level);

    IBinder onBind(Intent intent);

    boolean onUnbind(Intent intent);

    void onRebind(Intent intent);

    void onTaskRemoved(Intent rootIntent);
}
