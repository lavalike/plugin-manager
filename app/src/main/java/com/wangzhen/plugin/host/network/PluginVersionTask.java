package com.wangzhen.plugin.host.network;

import com.wangzhen.network.callback.RequestCallback;
import com.wangzhen.network.task.PostJsonTask;

/**
 * PluginVersionTask
 * Created by wangzhen on 2020/11/18.
 */
public class PluginVersionTask extends PostJsonTask {
    public <EntityType> PluginVersionTask(RequestCallback<EntityType> callback) {
        super(callback);
    }

    @Override
    public String getApi() {
        return "http://192.168.10.100:8080/wangzhen/plugin/plugin.json";
    }
}
