package com.wangzhen.plugin.proxy;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.wangzhen.plugin.PluginManager;
import com.wangzhen.plugin.callback.PluginActivityLifecycle;
import com.wangzhen.plugin.callback.PluginServiceLifecycle;
import com.wangzhen.plugin.common.Key;

/**
 * ProxyService
 * Created by wangzhen on 2020/4/9.
 */
public class ProxyService extends Service {

    private PluginServiceLifecycle mLifecycle;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLifecycle == null) {
            handleProxy(intent);
        }
        return mLifecycle != null ? mLifecycle.onStartCommand(intent, flags, startId) : super.onStartCommand(intent, flags, startId);
    }

    private void handleProxy(Intent intent) {
        String className = intent.getStringExtra(Key.CLASS_NAME);
        try {
            Class<?> pluginClass = PluginManager.getInstance().getPluginClassloader().loadClass(className);
            Object instance = pluginClass.newInstance();
            if (instance instanceof PluginActivityLifecycle) {
                mLifecycle = (PluginServiceLifecycle) instance;
                mLifecycle.attach(this);
                mLifecycle.onCreate();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (mLifecycle != null) {
            mLifecycle.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mLifecycle != null) {
            mLifecycle.onConfigurationChanged(newConfig);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        if (mLifecycle != null) {
            mLifecycle.onLowMemory();
        }
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        if (mLifecycle != null) {
            mLifecycle.onTrimMemory(level);
        }
        super.onTrimMemory(level);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mLifecycle == null) {
            handleProxy(intent);
        }
        return mLifecycle != null ? mLifecycle.onBind(intent) : null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mLifecycle != null ? mLifecycle.onUnbind(intent) : super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        if (mLifecycle != null) {
            mLifecycle.onRebind(intent);
        }
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (mLifecycle != null) {
            mLifecycle.onTaskRemoved(rootIntent);
        }
        super.onTaskRemoved(rootIntent);
    }
}
