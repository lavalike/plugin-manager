package com.wangzhen.plugin.callback;

import android.app.Activity;
import android.os.Bundle;

/**
 * plugin lifecycle callbacks
 * Created by wangzhen on 2020/4/1.
 */
public interface PluginLifecycle {
    void attach(Activity activity);

    void onCreate(Bundle bundle);

    void onStart();

    void onRestart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
