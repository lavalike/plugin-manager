package com.wangzhen.plugin.host;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SchemeActivity
 * Created by wangzhen on 2020/4/1.
 */
public class SchemeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);
        setTitle("scheme activity");
    }
}
