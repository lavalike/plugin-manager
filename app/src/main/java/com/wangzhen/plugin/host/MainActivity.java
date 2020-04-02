package com.wangzhen.plugin.host;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wangzhen.plugin.PluginManager;
import com.wangzhen.plugin.callback.PluginLoadCallback;

/**
 * MainActivity
 * Created by wangzhen on 2020/4/1.
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvMsg = findViewById(R.id.tv_msg);
    }

    public void loadPluginOne(View view) {
        mTvMsg.setText("Plugin Load Status:\n");
        PluginManager.getInstance().loadAsset("plugin/", "plugin-one.apk", new PluginLoadCallback() {
            @Override
            public void onSuccess() {
                mTvMsg.append("plugin-one.apk load success");
                PluginManager.getInstance().startActivity();
            }

            @Override
            public void onFail(String error) {
                mTvMsg.append("plugin-one.apk load fail: " + error);
                Toast.makeText(MainActivity.this, "plugin-one load fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadPluginTwo(View view) {
        mTvMsg.setText("Plugin Load Status:\n");
        PluginManager.getInstance().loadAsset("plugin/", "plugin-two.apk", new PluginLoadCallback() {
            @Override
            public void onSuccess() {
                mTvMsg.append("plugin-two.apk load success");
                PluginManager.getInstance().startActivity();
            }

            @Override
            public void onFail(String error) {
                mTvMsg.append("plugin-two.apk load fail: " + error);
                Toast.makeText(MainActivity.this, "plugin-two load fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
