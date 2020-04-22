package com.wangzhen.plugin.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.wangzhen.plugin.callback.PluginActivityLifecycle;

/**
 * all activities in plugin must extend this
 * Created by wangzhen on 2020/4/1.
 */
public class PluginBaseActivity extends FragmentActivity implements PluginActivityLifecycle {
    protected Activity mProxy;

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

    public Context getActivity() {
        return mProxy == null ? this : mProxy;
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
    public void onDestroy() {
        if (mProxy == null) {
            super.onDestroy();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mProxy != null) {
            mProxy.setContentView(layoutResID);
        } else {
            super.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View view) {
        if (mProxy != null) {
            mProxy.setContentView(view);
        } else {
            super.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (mProxy != null) {
            mProxy.setContentView(view, params);
        } else {
            super.setContentView(view, params);
        }
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

    @Override
    public PackageManager getPackageManager() {
        if (mProxy != null) {
            return mProxy.getPackageManager();
        }
        return super.getPackageManager();
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
        } else {
            super.finish();
        }
    }

    @Override
    public Intent getIntent() {
        if (mProxy != null) {
            return mProxy.getIntent();
        }
        return super.getIntent();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (mProxy != null) {
            return mProxy.getSharedPreferences(name, mode);
        }
        return super.getSharedPreferences(name, mode);
    }

    @Override
    public Context getApplicationContext() {
        if (mProxy != null) {
            return mProxy.getApplicationContext();
        }
        return super.getApplicationContext();
    }

    @Override
    public void startActivity(Intent intent) {
        if (mProxy != null) {
            mProxy.startActivity(intent);
        } else {
            super.startActivity(intent);
        }
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        if (mProxy != null) {
            mProxy.startActivity(intent, options);
        } else {
            super.startActivity(intent, options);
        }
    }

    @Override
    public ComponentName startService(Intent service) {
        if (mProxy != null) {
            return mProxy.startService(service);
        }
        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        if (mProxy != null) {
            return mProxy.stopService(name);
        }
        return super.stopService(name);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (mProxy != null) {
            mProxy.startActivityForResult(intent, requestCode);
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mProxy == null) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (mProxy == null) {
            super.onBackPressed();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        if (mProxy == null) {
            super.onRestoreInstanceState(savedInstanceState, persistentState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (mProxy == null) {
            super.onSaveInstanceState(outState, outPersistentState);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mProxy == null) {
            super.onNewIntent(intent);
        }
    }

    @Override
    public void sendBroadcast(Intent intent) {
        if (mProxy != null) {
            mProxy.sendBroadcast(intent);
        } else {
            super.sendBroadcast(intent);
        }
    }

    @Override
    public void sendBroadcast(Intent intent, String receiverPermission) {
        if (mProxy != null) {
            mProxy.sendBroadcast(intent, receiverPermission);
        } else {
            super.sendBroadcast(intent, receiverPermission);
        }
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        if (mProxy != null) {
            mProxy.sendOrderedBroadcast(intent, receiverPermission);
        } else {
            super.sendOrderedBroadcast(intent, receiverPermission);
        }
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        if (mProxy != null) {
            mProxy.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        } else {
            super.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (mProxy != null) {
            return mProxy.registerReceiver(receiver, filter);
        }
        return super.registerReceiver(receiver, filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        if (mProxy != null) {
            return mProxy.registerReceiver(receiver, filter, flags);
        }
        return super.registerReceiver(receiver, filter, flags);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        if (mProxy != null) {
            return mProxy.registerReceiver(receiver, filter, broadcastPermission, scheduler);
        }
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) {
        if (mProxy != null) {
            return mProxy.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
        }
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (mProxy != null) {
            mProxy.unregisterReceiver(receiver);
        } else {
            super.unregisterReceiver(receiver);
        }
    }
}
