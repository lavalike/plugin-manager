package com.wangzhen.plugin.callback;

/**
 * a callback for plugins`s loading status
 * Created by wangzhen on 2020/4/1.
 */
public interface PluginLoadCallback {
    /**
     * plugin load success
     */
    void onSuccess();

    /**
     * plugin load fail
     *
     * @param error error
     */
    void onFail(String error);
}
