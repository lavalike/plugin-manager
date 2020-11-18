package com.wangzhen.plugin.host;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wangzhen.download.DownloadClient;
import com.wangzhen.download.bean.ParamsBody;
import com.wangzhen.download.callback.OnDownloadCallback;
import com.wangzhen.network.callback.LoadingCallback;
import com.wangzhen.plugin.PluginManager;
import com.wangzhen.plugin.callback.PluginLoadCallback;
import com.wangzhen.plugin.host.entity.VersionEntity;
import com.wangzhen.plugin.host.network.PluginVersionTask;
import com.wangzhen.plugin.util.FileUtils;

import java.io.File;

/**
 * MainActivity
 * Created by wangzhen on 2020/4/1.
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTvMsg;
    private BroadcastReceiver mReceiver;
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initReceiver();
    }

    private void initReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("plugin-two".equals(intent.getAction())) {
                    Toast.makeText(context, "宿主收到插件广播 -> " + intent.getStringExtra("data"), Toast.LENGTH_SHORT).show();
                } else if ("launch_plugin".equals(intent.getAction())) {
                    Toast.makeText(context, "宿主调起另一插件", Toast.LENGTH_SHORT).show();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("plugin-two");
        filter.addAction("launch_plugin");
        registerReceiver(mReceiver, filter);
    }

    private void initViews() {
        mTvMsg = findViewById(R.id.tv_msg);
        mTvMsg.setText("Status:\n");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_plugin:
                showLoading();
                PluginManager.getInstance().loadAsset("plugin/plugin-one.apk", new PluginLoadCallback() {
                    @Override
                    public void onSuccess() {
                        stopLoading();
                        mTvMsg.append("plugin-one.apk load success\n");
                        PluginManager.getInstance().startActivity();
                    }

                    @Override
                    public void onFail(String error) {
                        stopLoading();
                        mTvMsg.append("plugin-one.apk load fail: " + error + "\n");
                    }
                });
                break;
            case R.id.btn_plugin_net:
                checkPluginVersion();
                break;
        }
    }

    private void showLoading() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog = null;
        }
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.show();
    }

    private void stopLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    private void checkPluginVersion() {
        new PluginVersionTask(new LoadingCallback<VersionEntity>() {
            @Override
            public void onSuccess(VersionEntity data) {
                int newCode = data.version_code;
                if (newCode > getPreferences().getInt("version_code", 0)) {
                    mTvMsg.append("new version found\n");
                    getPreferences().edit().putInt("version_code", newCode).apply();
                    download(data.url);
                } else {
                    mTvMsg.append("no new version found, load cache version\n");
                    loadPlugin(getPluginCache());
                }
            }

            @Override
            public void onError(int code, String message) {
                mTvMsg.append(message + "\n");
                mTvMsg.append("an error occur, load cache version\n");
                loadPlugin(getPluginCache());
            }
        }).setTag(this).exe();
    }

    private String getPluginCache() {
        return getPreferences().getString("path", "");
    }

    private void download(String url) {
        if (TextUtils.isEmpty(url)) {
            mTvMsg.append("plugin url empty, download stop.\n");
            return;
        }
        mTvMsg.append("download new plugin\n");
        DownloadClient.get().enqueue(new ParamsBody.Builder().url(url).dir(FileUtils.getInternalPluginPath(this))
                .callback(new OnDownloadCallback() {
                    @Override
                    public void onLoading(int progress) {

                    }

                    @Override
                    public void onSuccess(String path) {
                        mTvMsg.append("plugin download success\n");
                        getPreferences().edit().putString("path", path).apply();
                        loadPlugin(path);
                    }

                    @Override
                    public void onFail(String err) {
                        loadPlugin(getPluginCache());
                    }
                })
                .build());
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences("plugin", Context.MODE_PRIVATE);
    }

    private void loadPlugin(String path) {
        File file = new File(path);
        if (file.exists()) {
            PluginManager.getInstance().load(path, new PluginLoadCallback() {
                @Override
                public void onSuccess() {
                    mTvMsg.append("plugin load success\n");
                    PluginManager.getInstance().startActivity();

//                    //开启服务
//                    Intent service = new Intent();
//                    service.setComponent(new ComponentName("com.wangzhen.plugin.two", "com.wangzhen.plugin.two.service.PluginService"));
//                    startService(service);
                }

                @Override
                public void onFail(String error) {
                    mTvMsg.append("plugin load fail: " + error + "\n");
                }
            });
        } else {
            mTvMsg.append("plugin not exist, stop load.\n");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
