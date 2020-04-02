package com.wangzhen.plugin.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangzhen.plugin.callback.PluginLifecycle;

/**
 * all activities in plugin must extend this
 * Created by wangzhen on 2020/4/1.
 */
public class PluginBaseActivity extends Activity implements PluginLifecycle {
    private Activity mProxy;

    @Override
    public void attach(Activity activity) {
        mProxy = activity;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        if (mProxy == null) {
            super.onCreate(saveInstanceState);
        }
    }

    @Override
    public void onStart() {
        if (mProxy == null) {
            super.onStart();
        }
    }

    @Override
    public void onRestart() {
        if (mProxy == null) {
            super.onRestart();
        }
    }

    @Override
    public void onResume() {
        if (mProxy == null) {
            super.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mProxy == null) {
            super.onPause();
        }
    }

    @Override
    public void onStop() {
        if (mProxy == null) {
            super.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        if (mProxy == null) {
            super.onDestroy();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mProxy != null) {
            mProxy.setContentView(layoutResID);
            return;
        }
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        if (mProxy != null) {
            mProxy.setContentView(view);
            return;
        }
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (mProxy != null) {
            mProxy.setContentView(view, params);
            return;
        }
        super.setContentView(view, params);
    }

    @Override
    public Window getWindow() {
        if (mProxy != null) {
            return mProxy.getWindow();
        }
        return super.getWindow();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        if (mProxy != null) {
            return mProxy.findViewById(id);
        }
        return super.findViewById(id);
    }

    @Override
    public WindowManager getWindowManager() {
        if (mProxy != null) {
            return mProxy.getWindowManager();
        }
        return super.getWindowManager();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        if (mProxy != null) {
            return mProxy.getApplicationInfo();
        }
        return super.getApplicationInfo();
    }

    @Override
    public void startActivity(Intent intent) {
        if (mProxy != null) {
            mProxy.startActivity(intent);
            return;
        }
        super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        if (mProxy != null) {
            mProxy.startActivity(intent, options);
            return;
        }
        super.startActivity(intent, options);
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mProxy != null) {
            return mProxy.getClassLoader();
        }
        return super.getClassLoader();
    }

    @Override
    public Resources getResources() {
        if (mProxy != null) {
            return mProxy.getResources();
        }
        return super.getResources();
    }

    @Override
    public String getPackageName() {
        if (mProxy != null) {
            return mProxy.getPackageName();
        }
        return super.getPackageName();
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        if (mProxy != null) {
            return mProxy.getLayoutInflater();
        }
        return super.getLayoutInflater();
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (mProxy != null) {
            return mProxy.getSystemService(name);
        }
        return super.getSystemService(name);
    }

    @Override
    public Context getBaseContext() {
        if (mProxy != null) {
            return mProxy.getBaseContext();
        }
        return super.getBaseContext();
    }

    @Override
    public void finish() {
        if (mProxy != null) {
            mProxy.finish();
            return;
        }
        super.finish();
    }
}
