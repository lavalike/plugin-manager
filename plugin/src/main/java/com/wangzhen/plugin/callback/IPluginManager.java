package com.wangzhen.plugin.callback;

import android.content.Context;
import android.content.res.Resources;

/**
 * IPluginManager
 * Created by wangzhen on 2020/4/1.
 */
public interface IPluginManager {
    /**
     * init plugin manager
     *
     * @param context application context
     */
    void init(Context context);

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
    void loadAsset(String pluginPath, String pluginName, OnLoadCallback callback);

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
    void load(String path, OnLoadCallback callback);

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
     * start activity according to given class name
     *
     * @param className class name
     */
    void startActivity(String className);

    /**
     * start the first activity declared in AndroidManifest.xml
     */
    void startActivity();
}