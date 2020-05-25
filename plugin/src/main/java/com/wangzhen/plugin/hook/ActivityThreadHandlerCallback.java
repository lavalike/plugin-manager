package com.wangzhen.plugin.hook;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.wangzhen.plugin.PluginManager;

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
            Object mClientTransaction = msg.obj;
            Field field = mClientTransaction.getClass().getDeclaredField("mActivityCallbacks");
            field.setAccessible(true);
            List mActivityCallbacks = (List) field.get(mClientTransaction);
            if (mActivityCallbacks.size() > 0) {
                String className = "android.app.servertransaction.LaunchActivityItem";
                if (className.equals(mActivityCallbacks.get(0).getClass().getCanonicalName())) {
                    Object mLaunchActivityItem = mActivityCallbacks.get(0);
                    Field intentField = mLaunchActivityItem.getClass().getDeclaredField("mIntent");
                    intentField.setAccessible(true);
                    Intent intent = (Intent) intentField.get(mLaunchActivityItem);
                    Intent target = intent.getParcelableExtra(HookHelper.EXTRA_TARGET_INTENT);
                    if (target != null) {
                        ComponentName component = target.getComponent();
                        if (component != null) {
                            PluginManager.getInstance().resolveTheme(component.getClassName());
                            intent.setComponent(component);
                            intent.putExtra("data", "restored by hook v28+");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLaunch(Message msg) {
        try {
            Object mActivityClientRecord = msg.obj;//ActivityClientRecord
            Field intentField = mActivityClientRecord.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            Intent intent = (Intent) intentField.get(mActivityClientRecord);
            Intent target = intent.getParcelableExtra(HookHelper.EXTRA_TARGET_INTENT);
            if (target != null) {
                ComponentName component = target.getComponent();
                if (component != null) {
                    PluginManager.getInstance().resolveTheme(component.getClassName());
                    intent.setComponent(component);
                    intent.putExtra("data", "restored by hook v28-");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
