package com.wangzhen.plugin.helper;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

/**
 * FileUtils
 * Created by wangzhen on 2020/4/1.
 */
public class FileUtils {

    private static final String PLUGIN_DIR = "plugin";

    /**
     * get internal plugin path : (/data/data/{package}/plugin/)
     *
     * @param context context
     * @return internal path
     */
    public static String getInternalPluginPath(Context context) {
        return context.getDir(PLUGIN_DIR, Context.MODE_PRIVATE).getAbsolutePath();
    }

    /**
     * get plugin file
     *
     * @param context context
     * @return plugin file
     */
    public static File getPluginFile(Context context, String pluginName) {
        return new File(getInternalPluginPath(context), pluginName);
    }

    /**
     * get dex output dir
     *
     * @param context context
     * @return dex output dir
     */
    public static File getDexOutputDir(Context context) {
        return context.getDir("dex", Context.MODE_PRIVATE);
    }

    /**
     * get file name
     *
     * @param path file path
     * @return file name
     */
    public static String getFileName(String path) {
        if (!TextUtils.isEmpty(path))
            return new File(path).getName();
        return "";
    }
}
