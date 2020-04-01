package com.wangzhen.plugin.base;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wangzhen.plugin.proxy.Plugin;

/**
 * all activities in plugin must extend this
 * Created by wangzhen on 2020/4/1.
 */
public class PluginBaseActivity extends Activity implements Plugin {
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
}
