package com.wangzhen.plugin.host;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
                checkPluginVersion();
            }
        } else {
            checkPluginVersion();
        }
    }

    private void checkPluginVersion() {
        Request request = new Request.Builder()
                .url("http://192.168.188.199:8080/wangzhen/plugin/plugin.json")
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvMsg.append("an error occur, load cache version\n");
                        downloadAndLoad(getPreferences().getString("url", ""));
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.code() == 200 && response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                int version_code = jsonObject.getInt("version_code");
                                String url = jsonObject.getString("url");
                                if (version_code > getPreferences().getInt("version_code", 0)) {
                                    mTvMsg.append("new version found\n");
                                    //更新版本
                                    getPreferences().edit().putInt("version_code", version_code).putString("url", url).apply();
                                    downloadAndLoad(url);
                                } else {
                                    //加载旧版本
                                    mTvMsg.append("no new version found, load cache version\n");
                                    downloadAndLoad(getPreferences().getString("url", ""));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mTvMsg.append("an error occur, load cache version\n");
                            downloadAndLoad(getPreferences().getString("url", ""));
                        }
                    }
                });
            }
        });
    }

    private void downloadAndLoad(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String path = FileUtils.getInternalPluginPath(this);
        File file;
        if ((file = new File(path, FileUtils.getFileName(url))).exists()) {
            mTvMsg.append("plugin exists, load cache plugin\n");
            loadPlugin(file.getAbsolutePath());
            return;
        }
        DownloadClient.get().enqueue(new ParamsBody.Builder().url(url).dir(path)
                .callback(new OnDownloadCallback() {
                    @Override
                    public void onLoading(int progress) {

                    }

                    @Override
                    public void onSuccess(String path) {
                        mTvMsg.append("plugin download success\n");
                        loadPlugin(path);
                    }

                    @Override
                    public void onFail(String err) {
                        mTvMsg.append("plugin download fail: " + err + "\n");
                    }
                })
                .build());
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences("plugin", Context.MODE_PRIVATE);
    }

    private void loadPlugin(String path) {
        PluginManager.getInstance().load(path, new PluginLoadCallback() {
            @Override
            public void onSuccess() {
                mTvMsg.append("plugin load success\n");
                PluginManager.getInstance().startActivity();
            }

            @Override
            public void onFail(String error) {
                mTvMsg.append("plugin load fail: " + error + "\n");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPluginVersion();
            } else {
                Toast.makeText(this, "请开启存储权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
