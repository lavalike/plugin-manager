package com.wangzhen.plugin.host;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wangzhen.plugin.PluginManager;

/**
 * MainActivity
 * Created by wangzhen on 2020/4/1.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PluginManager.getInstance().init(this);
    }

    public void loadPluginOne(View view) {
        PluginManager.getInstance().loadAsset("plugin/", "plugin-one.apk");
        PluginManager.getInstance().startActivity();
    }

    public void loadPluginTwo(View view) {
        PluginManager.getInstance().loadAsset("plugin/", "plugin-two.apk");
        PluginManager.getInstance().startActivity();
    }
}
