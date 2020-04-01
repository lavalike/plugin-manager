package com.wangzhen.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.wangzhen.plugin.callback.IPluginManager;
import com.wangzhen.plugin.common.Key;
import com.wangzhen.plugin.helper.CopyUtils;
import com.wangzhen.plugin.helper.PathUtils;
import com.wangzhen.plugin.proxy.ProxyActivity;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    private PluginManager() {
    }

    public static IPluginManager getInstance() {
        return sInstance;
    }

    /**
     * init plugin manager
     *
     * @param context application context
     */
    @Override
    public void init(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /**
     * load apk from asset by name
     *
     * @param pluginPath pluginPath
     * @param pluginName pluginName
     */
    @Override
    public void loadAsset(String pluginPath, String pluginName) {
        File plugin = PathUtils.getPluginFile(mContext, pluginName);
        if (plugin.exists()) {
            boolean ignore = plugin.delete();
        }
        if (CopyUtils.copyAsset(mContext, pluginPath + pluginName, plugin.getAbsolutePath())) {
            load(plugin.getAbsolutePath());
        }
    }

    /**
     * load apk from local path
     *
     * @param path path
     */
    @Override
    public void load(String path) {
        createClassloader(path);
        addAssetPath(path);
    }

    /**
     * create classloader for plugin
     *
     * @param path plugin file path
     */
    private void createClassloader(String path) {
        mPluginDexClassloader = new DexClassLoader(path, PathUtils.getDexOutputDir(mContext).getAbsolutePath(), null, mContext.getClassLoader());
    }

    /**
     * add plugin to asset path
     *
     * @param path path
     */
    private void addAssetPath(String path) {
        AssetManager assetManager = null;
        try {
            assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getMethod("addAssetPath", String.class);
            method.invoke(assetManager, path);
            mPluginResources = new Resources(assetManager, mContext.getResources().getDisplayMetrics(), mContext.getResources().getConfiguration());
            mPackageArchiveInfo = mContext.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * get classloader of plugin
     *
     * @return classloader
     */
    @Override
    public ClassLoader getPluginClassloader() {
        return mPluginDexClassloader;
    }

    /**
     * get resources for plugin
     *
     * @return resources
     */
    @Override
    public Resources getPluginResources() {
        return mPluginResources;
    }

    /**
     * start activity according to given class name
     *
     * @param className class name
     */
    @Override
    public void startActivity(String className) {
        Intent intent = new Intent(mContext, ProxyActivity.class);
        intent.putExtra(Key.CLASS_NAME, className);
        mContext.startActivity(intent);
    }

    /**
     * start default main activity
     */
    @Override
    public void startActivity() {
        if (mPackageArchiveInfo != null) {
            startActivity(mPackageArchiveInfo.activities[0].name);
        }
    }
}
