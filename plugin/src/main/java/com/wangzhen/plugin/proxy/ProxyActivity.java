package com.wangzhen.plugin.proxy;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wangzhen.plugin.PluginManager;
import com.wangzhen.plugin.callback.PluginLifecycle;
import com.wangzhen.plugin.common.Key;

/**
 * ProxyActivity
 * Created by wangzhen on 2020/4/1.
 */
public class ProxyActivity extends AppCompatActivity {

    private PluginLifecycle mLifecycle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleProxy();
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
}
