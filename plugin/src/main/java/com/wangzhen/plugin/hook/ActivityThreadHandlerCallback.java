package com.wangzhen.plugin.hook;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.util.List;

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
        switch (msg.what) {
            case 100:
                // 9.0以前100
                handleLaunch(msg);
                break;
            case 159:
                // 9.0以后159
                handleLaunchV28(msg);
                break;
        }
        handler.handleMessage(msg);
        return true;
    }

    private void handleLaunchV28(Message msg) {
        try {
            Object obj = msg.obj;
            Field field = obj.getClass().getDeclaredField("mActivityCallbacks");
            field.setAccessible(true);
            List<Object> mActivityCallbacks = (List<Object>) field.get(obj);
            if (mActivityCallbacks.size() > 0) {
                String className = "android.app.servertransaction.LaunchActivityItem";
                if (className.equals(mActivityCallbacks.get(0).getClass().getCanonicalName())) {
                    Object object = mActivityCallbacks.get(0);
                    Field intentField = object.getClass().getDeclaredField("mIntent");
                    intentField.setAccessible(true);
                    Intent intent = (Intent) intentField.get(object);
                    Intent target = intent.getParcelableExtra(HookHelper.EXTRA_TARGET_INTENT);
                    intent.setComponent(target.getComponent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLaunch(Message msg) {
        try {
            Object obj = msg.obj;//ActivityClientRecord
            Field intentField = obj.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            Intent proxyIntent = (Intent) intentField.get(obj);
            Intent realIntent = proxyIntent.getParcelableExtra(HookHelper.EXTRA_TARGET_INTENT);
            if (realIntent != null) {
                proxyIntent.setComponent(realIntent.getComponent());
                proxyIntent.putExtra("data", "restored by hook");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
