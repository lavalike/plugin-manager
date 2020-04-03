package com.wangzhen.plugin.host;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.wangzhen.download.DownloadClient;
import com.wangzhen.download.bean.ParamsBody;
import com.wangzhen.download.callback.OnDownloadCallback;
import com.wangzhen.plugin.PluginManager;
import com.wangzhen.plugin.callback.PluginLoadCallback;
import com.wangzhen.plugin.helper.FileUtils;

/**
 * MainActivity
 * Created by wangzhen on 2020/4/1.
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTvMsg;
    private int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvMsg = findViewById(R.id.tv_msg);
        mTvMsg.setText("Status:\n");
    }

    public void loadAssetPlugin(View view) {
        PluginManager.getInstance().loadAsset("plugin/plugin-one.apk", new PluginLoadCallback() {
            @Override
            public void onSuccess() {
                mTvMsg.append("plugin-one.apk load success\n");
                PluginManager.getInstance().startActivity();
            }

            @Override
            public void onFail(String error) {
                mTvMsg.append("plugin-one.apk load fail: " + error + "\n");
            }
        });
    }

    public void loadNetPlugin(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                downloadAndInstall();
            }
        } else {
            downloadAndInstall();
        }
    }

    private void downloadAndInstall() {
        DownloadClient.get().enqueue(new ParamsBody.Builder()
                .url("http://192.168.188.199:8080/wangzhen/plugin/apk/plugin-two.apk")
                .dir(FileUtils.getInternalPluginPath(this))
                .callback(new OnDownloadCallback() {
                    @Override
                    public void onLoading(int progress) {

                    }

                    @Override
                    public void onSuccess(String path) {
                        mTvMsg.append("plugin download success\n");
                        PluginManager.getInstance().load(path, new PluginLoadCallback() {
                            @Override
                            public void onSuccess() {
                                mTvMsg.append("plugin-two.apk load success\n");
                                PluginManager.getInstance().startActivity();
                            }

                            @Override
                            public void onFail(String error) {
                                mTvMsg.append("plugin-two.apk load fail: " + error + "\n");
                            }
                        });
                    }

                    @Override
                    public void onFail(String err) {
                        mTvMsg.append("plugin download fail: " + err + "\n");
                    }
                })
                .build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadAndInstall();
            } else {
                Toast.makeText(this, "请开启存储权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
