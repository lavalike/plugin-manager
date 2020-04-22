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
     * get optimized dir
     *
     * @param context context
     * @return dex output dir
     */
    public static File getOptimizedDir(Context context) {
        return context.getDir("dex", Context.MODE_PRIVATE);
    }

    /**
     * get library search dir
     *
     * @param context context
     * @return library search dir
     */
    public static File getLibrarySearchDir(Context context) {
        return context.getDir("native_lib", Context.MODE_PRIVATE);
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

    /**
     * delete dir and files under dir
     *
     * @param dir dir
     * @return result
     */
    public static boolean deleteDirectory(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                flag = deleteFile(file);
                if (!flag) break;
            } else {
                flag = deleteDirectory(file);
                if (!flag) break;
            }
        }
        if (!flag) return false;
        return dir.delete();
    }

    /**
     * delete single file
     *
     * @param file file
     * @return result
     */
    public static boolean deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
}
