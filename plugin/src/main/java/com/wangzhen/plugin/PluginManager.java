package com.wangzhen.plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.wangzhen.plugin.callback.Plugin;
import com.wangzhen.plugin.callback.PluginLoadCallback;
import com.wangzhen.plugin.util.CopyUtils;
import com.wangzhen.plugin.util.CustomClassLoader;
import com.wangzhen.plugin.util.FileUtils;
import com.wangzhen.plugin.hook.HookHelper;
import com.wangzhen.plugin.hook.ServiceManager;
import com.wangzhen.plugin.provider.ContextProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;

import dalvik.system.DexClassLoader;

/**
 * plugin-manager framework lite.
 * Created by wangzhen on 2020/4/1.
 */
public final class PluginManager implements Plugin {
    private Context mContext;
    private static Plugin sInstance;
    private DexClassLoader mPluginDexClassloader;
    private Resources mPluginResources;
    private PackageInfo mPackageArchiveInfo;
    private String mPath;
    private PluginLoadCallback mCallback;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AssetManager mAssetManager;
    private Resources.Theme mTheme;

    static {
        sInstance = new PluginManager();
    }

    private PluginManager() {
        mContext = ContextProvider.sContext;
    }

    public static Plugin getInstance() {
        return sInstance;
    }

    @Override
    public void loadAsset(String path) {
        loadAsset(path, null);
    }

    @Override
    public void loadAsset(String path, PluginLoadCallback callback) {
        String pluginName = FileUtils.getFileName(path);
        if (TextUtils.isEmpty(pluginName)) {
            if (callback != null) {
                callback.onFail("wrong plugin name");
            }
            return;
        }
        File plugin = FileUtils.getPluginFile(mContext, pluginName);
        if (plugin.exists()) {
            boolean ignore = plugin.delete();
        }
        if (CopyUtils.copyAsset(mContext, path, plugin.getAbsolutePath())) {
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
    public void load(String path, PluginLoadCallback callback) {
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
        try {
            mPluginDexClassloader = new CustomClassLoader(path, FileUtils.getOptimizedDir(mContext).getAbsolutePath(), null, mContext.getClassLoader());
            mAssetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getMethod("addAssetPath", String.class);
            method.invoke(mAssetManager, path);
            mPluginResources = new Resources(mAssetManager, mContext.getResources().getDisplayMetrics(), mContext.getResources().getConfiguration());
            mPackageArchiveInfo = mContext.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);

//            mContext.setTheme(mPackageArchiveInfo.applicationInfo.theme);
//            mTheme = PluginManager.getInstance().getPluginResources().newTheme();
//            mTheme.setTo(mContext.getTheme());

            HookHelper.hook();
            File file = new File(path);
            HookHelper.patchClassLoader(mContext.getClassLoader(), file, mContext.getFileStreamPath(file.getName() + ".odex"));
            ServiceManager.getInstance().parseServices();

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
    public PackageInfo getPluginPackageInfo() {
        return mPackageArchiveInfo;
    }

    @Override
    public void startActivity(String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(mContext, className));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void startActivity() {
        if (mPackageArchiveInfo != null) {
            startActivity(mPackageArchiveInfo.activities[0].name);
        }
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager;
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme;
    }

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
