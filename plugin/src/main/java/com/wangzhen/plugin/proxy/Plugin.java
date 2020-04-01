package com.wangzhen.plugin.proxy;

import android.app.Activity;
import android.os.Bundle;

/**
 * plugin lifecycle callbacks
 * Created by wangzhen on 2020/4/1.
 */
public interface Plugin {
    void attach(Activity activity);

    void onCreate(Bundle bundle);

    void onStart();

    void onRestart();

    void onResume();

    void onPause();

    void onStop();
}
