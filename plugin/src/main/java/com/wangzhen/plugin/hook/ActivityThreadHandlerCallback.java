package com.wangzhen.plugin.hook;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;

/**
 * ActivityThreadHandlerCallback
 * Created by wangzhen on 2020/4/23.
 */
public class ActivityThreadHandlerCallback implements Handler.Callback {
    private Handler handler;

    public ActivityThreadHandlerCallback(Handler handler) {
        this.handler = handler;
    }

    @Override
    public boolean handleMessage(Message msg) {
        // 9.0以前100
        // 9.0以后159
        if (msg.what == 100 || msg.what == 159) {
            handleLaunchActivity(msg);
        }
        handler.handleMessage(msg);
        return true;
    }

    private void handleLaunchActivity(Message msg) {
        Object obj = msg.obj;//ActivityClientRecord
        try {
            Field intentField = obj.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            Intent proxyIntent = (Intent) intentField.get(obj);
            Intent realIntent = proxyIntent.getParcelableExtra(HookHelper.EXTRA_TARGET_INTENT);
            if (realIntent != null) {
                proxyIntent.setComponent(realIntent.getComponent());
                proxyIntent.putExtra("data", "restored by hook");
            }
        } catch (Exception ignore) {

        }

    }
}
