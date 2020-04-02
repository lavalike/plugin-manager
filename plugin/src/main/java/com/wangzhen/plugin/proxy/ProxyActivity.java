package com.wangzhen.plugin.proxy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.wangzhen.plugin.PluginManager;
import com.wangzhen.plugin.callback.PluginLifecycle;
import com.wangzhen.plugin.common.Key;

/**
 * ProxyActivity
 * Created by wangzhen on 2020/4/1.
 */
public class ProxyActivity extends Activity {

    private PluginLifecycle mLifecycle;
    private Resources.Theme mTheme;
    private ActivityInfo mActivityInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        compat();
        handleProxy();
    }

    private void compat() {
        PackageInfo packageInfo = PluginManager.getInstance().getPluginPackageInfo();
        if (packageInfo == null) {
            return;
        }
        String className = packageInfo.activities[0].name;
        int defaultTheme = packageInfo.applicationInfo.theme;
        for (ActivityInfo activity : packageInfo.activities) {
            if (activity.name.equals(className)) {
                mActivityInfo = activity;
                if (mActivityInfo.theme == 0) {
                    if (defaultTheme != 0) {
                        mActivityInfo.theme = defaultTheme;
                    } else {
                        if (Build.VERSION.SDK_INT >= 14) {
                            mActivityInfo.theme = android.R.style.Theme_DeviceDefault;
                        } else {
                            mActivityInfo.theme = android.R.style.Theme;
                        }
                    }
                }
            }
        }
        //Theme.
        setTheme(mActivityInfo.theme);

        Resources.Theme superTheme = getTheme();
        mTheme = PluginManager.getInstance().getPluginResources().newTheme();
        mTheme.setTo(superTheme);
        try {
            mTheme.applyStyle(mActivityInfo.theme, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleProxy() {
        String className = getIntent().getStringExtra(Key.CLASS_NAME);
        try {
            Class<?> pluginClass = PluginManager.getInstance().getPluginClassloader().loadClass(className);
            Object instance = pluginClass.newInstance();
            if (instance instanceof PluginLifecycle) {
                mLifecycle = (PluginLifecycle) instance;
                mLifecycle.attach(this);
                Bundle bundle = new Bundle();
                mLifecycle.onCreate(bundle);
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
    protected void onStart() {
        mLifecycle.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        mLifecycle.onResume();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        mLifecycle.onRestart();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        mLifecycle.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mLifecycle.onStop();
        super.onStop();
    }

    @Override
    public void startActivity(Intent intent) {
        String className = intent.getComponent() != null ? intent.getComponent().getClassName() : "";
        intent = new Intent(this, ProxyActivity.class);
        intent.putExtra(Key.CLASS_NAME, className);
        super.startActivity(intent);
    }

    @Override
    public ClassLoader getClassLoader() {
        ClassLoader classloader = PluginManager.getInstance().getPluginClassloader();
        return classloader != null ? classloader : super.getClassLoader();
    }

    @Override
    public Resources getResources() {
        Resources resources = PluginManager.getInstance().getPluginResources();
        return resources != null ? resources : super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        AssetManager assets = PluginManager.getInstance().getAssets();
        return assets != null ? assets : super.getAssets();
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme != null ? mTheme : super.getTheme();
    }
}
