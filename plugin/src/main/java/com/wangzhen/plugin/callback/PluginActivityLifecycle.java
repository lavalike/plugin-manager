package com.wangzhen.plugin.callback;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

/**
 * plugin activity lifecycle callbacks
 * Created by wangzhen on 2020/4/1.
 */
public interface PluginActivityLifecycle {
    void attach(FragmentActivity activity);

    void onCreate(Bundle bundle);

    void onStart();

    void onRestart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
