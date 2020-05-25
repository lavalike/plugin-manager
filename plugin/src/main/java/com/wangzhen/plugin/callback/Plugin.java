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
     * load apk from asset
     *
     * @param path pluginPath
     */
    void loadAsset(String path);

    /**
     * load apk from asset with callback
     *
     * @param path     pluginPath
     * @param callback callback
     */
    void loadAsset(String path, PluginLoadCallback callback);

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
     * @param className plugin class name
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

    /**
     * resolve theme from activity class name
     *
     * @param className activity class name
     */
    void resolveTheme(String className);

    /**
     * get plugin theme
     *
     * @return theme
     */
    Resources.Theme getTheme();
}
