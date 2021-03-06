package com.wangzhen.plugin.proxy;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.wangzhen.plugin.PluginManager;
import com.wangzhen.plugin.R;
import com.wangzhen.plugin.callback.PluginActivityLifecycle;
import com.wangzhen.plugin.common.Key;

/**
 * ProxyActivity
 * Created by wangzhen on 2020/4/1.
 */
public class ProxyActivity extends FragmentActivity {

    private PluginActivityLifecycle mLifecycle;
    private String mClassName;
    private ActivityInfo mActivityInfo;
    private Resources.Theme mTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleProxy();
    }

    private void handleProxy() {
        mClassName = getIntent().getStringExtra(Key.CLASS_NAME);
        try {
            themeCompat();
            Class<?> pluginClass = PluginManager.getInstance().getPluginClassloader().loadClass(mClassName);
            Object instance = pluginClass.newInstance();
            if (instance instanceof PluginActivityLifecycle) {
                mLifecycle = (PluginActivityLifecycle) instance;
                mLifecycle.attach(this);
                Bundle bundle = new Bundle();
                mLifecycle.onCreate(bundle);
            }
        } catch (Exception e) {
            Log.e("TAG", "handleProxy exception -> " + e.getMessage());
            mLifecycle = null;
            finish();
        }
    }

    private void themeCompat() {
        PackageInfo packageInfo = PluginManager.getInstance().getPluginPackageInfo();
        if (packageInfo != null) {
            int defaultTheme = packageInfo.applicationInfo.theme;
            for (ActivityInfo info : packageInfo.activities) {
                if (info.name.equals(mClassName)) {
                    mActivityInfo = info;
                    if (mActivityInfo.theme == 0) {
                        if (defaultTheme != 0) {
                            mActivityInfo.theme = defaultTheme;
                        } else {
                            mActivityInfo.theme = R.style.Theme_AppCompat;
                        }
                    }
                    break;
                }
            }
            if (mActivityInfo != null) {
                if (mActivityInfo.theme > 0) {
                    setTheme(mActivityInfo.theme);
                }
                Resources.Theme superTheme = getTheme();
                mTheme = PluginManager.getInstance().getPluginResources().newTheme();
                mTheme.setTo(superTheme);
                try {
                    // Finals适配三星以及部分加载XML出现异常BUG
                    mTheme.applyStyle(mActivityInfo.theme, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        if (mLifecycle != null) {
            mLifecycle.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (mLifecycle != null) {
            mLifecycle.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onRestart() {
        if (mLifecycle != null) {
            mLifecycle.onRestart();
        }
        super.onRestart();
    }

    @Override
    protected void onPause() {
        if (mLifecycle != null) {
            mLifecycle.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mLifecycle != null) {
            mLifecycle.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mLifecycle != null) {
            mLifecycle.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void startActivity(Intent intent) {
        if (TextUtils.isEmpty(intent.getAction())) {
            String className = intent.getComponent() != null ? intent.getComponent().getClassName() : "";
            intent = new Intent(this, ProxyActivity.class);
            intent.putExtra(Key.CLASS_NAME, className);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mLifecycle != null) {
            mLifecycle.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mLifecycle != null) {
            mLifecycle.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mLifecycle != null) {
            mLifecycle.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mLifecycle != null) {
            mLifecycle.onNewIntent(intent);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (mLifecycle != null) {
            mLifecycle.onRestoreInstanceState(savedInstanceState);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (mLifecycle != null) {
            mLifecycle.onWindowAttributesChanged(params);
        }
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (mLifecycle != null) {
            mLifecycle.onWindowFocusChanged(hasFocus);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onBackPressed() {
        if (mLifecycle != null) {
            mLifecycle.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mLifecycle != null) {
            return mLifecycle.onCreateOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mLifecycle != null) {
            return mLifecycle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
