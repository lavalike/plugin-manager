package com.wangzhen.plugin.base;

import android.content.res.AssetManager;
import android.content.res.Resources;

import androidx.fragment.app.FragmentActivity;

import com.wangzhen.plugin.PluginManager;

/**
 * all activities in plugin must extend this
 * Created by wangzhen on 2020/4/1.
 */
public class PluginBaseActivity extends FragmentActivity {
    @Override
    public Resources getResources() {
        Resources resources = PluginManager.getInstance().getPluginResources();
        return resources != null ? resources : super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        AssetManager assets = PluginManager.getInstance().getAssets();
        return assets != null ? assets : super.getAssets();
    }
}
