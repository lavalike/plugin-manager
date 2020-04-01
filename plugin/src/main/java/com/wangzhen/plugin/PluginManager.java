package com.wangzhen.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

import com.wangzhen.plugin.callback.IPluginManager;
import com.wangzhen.plugin.callback.OnLoadCallback;
import com.wangzhen.plugin.common.Key;
import com.wangzhen.plugin.helper.CopyUtils;
import com.wangzhen.plugin.helper.PathUtils;
import com.wangzhen.plugin.proxy.ProxyActivity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;

import dalvik.system.DexClassLoader;

/**
 * plugin-manager framework lite.
 * Created by wangzhen on 2020/4/1.
 */
public final class PluginManager implements IPluginManager {
    private Context mContext;
    private static IPluginManager sInstance = new PluginManager();
    private DexClassLoader mPluginDexClassloader;
    private Resources mPluginResources;
    private PackageInfo mPackageArchiveInfo;
    private String mPath;
    private OnLoadCallback mCallback;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private PluginManager() {
    }

    public static IPluginManager getInstance() {
        return sInstance;
    }

    @Override
    public void init(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public void loadAsset(String pluginPath, String pluginName) {
        loadAsset(pluginPath, pluginName, null);
    }

    @Override
    public void loadAsset(String pluginPath, String pluginName, OnLoadCallback callback) {
        File plugin = PathUtils.getPluginFile(mContext, pluginName);
        if (plugin.exists()) {
            boolean ignore = plugin.delete();
        }
        if (CopyUtils.copyAsset(mContext, pluginPath + pluginName, plugin.getAbsolutePath())) {
            load(plugin.getAbsolutePath(), callback);
        } else {
            if (callback != null) {
                callback.onFail("copy fail");
            }
        }
    }

    @Override
    public void load(String path) {
        load(path, null);
    }

    @Override
    public void load(String path, OnLoadCallback callback) {
        mCallback = callback;
        mPath = path;
        Executors.newCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                applyPlugin(mPath);
            }
        });
    }

    private void applyPlugin(String path) {
        mPluginDexClassloader = new DexClassLoader(path, PathUtils.getDexOutputDir(mContext).getAbsolutePath(), null, mContext.getClassLoader());
        AssetManager assetManager = null;
        try {
            assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getMethod("addAssetPath", String.class);
            method.invoke(assetManager, path);
            mPluginResources = new Resources(assetManager, mContext.getResources().getDisplayMetrics(), mContext.getResources().getConfiguration());
            mPackageArchiveInfo = mContext.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallback != null) {
                        mCallback.onSuccess();
                    }
                }
            });
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallback != null) {
                        mCallback.onFail(e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public ClassLoader getPluginClassloader() {
        return mPluginDexClassloader;
    }

    @Override
    public Resources getPluginResources() {
        return mPluginResources;
    }

    @Override
    public void startActivity(String className) {
        Intent intent = new Intent(mContext, ProxyActivity.class);
        intent.putExtra(Key.CLASS_NAME, className);
        mContext.startActivity(intent);
    }

    @Override
    public void startActivity() {
        if (mPackageArchiveInfo != null) {
            startActivity(mPackageArchiveInfo.activities[0].name);
        }
    }

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
