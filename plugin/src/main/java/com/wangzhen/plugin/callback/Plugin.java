package com.wangzhen.plugin.callback;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Plugin
 * Created by wangzhen on 2020/4/1.
 */
public interface Plugin {
    /**
     * load apk from asset by name
     *
     * @param pluginPath pluginPath
     * @param pluginName pluginName
     */
    void loadAsset(String pluginPath, String pluginName);

    /**
     * load apk from asset by name with callback
     *
     * @param pluginPath pluginPath
     * @param pluginName pluginName
     * @param callback   callback
     */
    void loadAsset(String pluginPath, String pluginName, PluginLoadCallback callback);

    /**
     * load apk from local path
     *
     * @param path path
     */
    void load(String path);

    /**
     * load apk from local path with callback
     *
     * @param path     path
     * @param callback callback
     */
    void load(String path, PluginLoadCallback callback);

    /**
     * get classloader of plugin
     *
     * @return classloader
     */
    ClassLoader getPluginClassloader();

    /**
     * get resources for plugin
     *
     * @return resources
     */
    Resources getPluginResources();

    /**
     * get package info for plugin
     *
     * @return package info
     */
    PackageInfo getPluginPackageInfo();

    /**
     * start activity according to given class name
     *
     * @param className class name
     */
    void startActivity(String className);

    /**
     * start the first activity declared in AndroidManifest.xml
     */
    void startActivity();

    /**
     * get plugin asset manager
     *
     * @return asset manager
     */
    AssetManager getAssets();
}
