package com.wangzhen.plugin.hook;

import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * ams hook helper.
 * Created by wangzhen on 2020/4/18.
 */
public class AMSHookHelper {
    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";

    public static void hookActivityManagerNative() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        Object gDefault;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            gDefault = gDefaultField.get(null);
        } else {
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            Field iActivityManagerSingleton = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            iActivityManagerSingleton.setAccessible(true);
            gDefault = iActivityManagerSingleton.get(null);
        }

        // gDefault is an instance of android.util.Singleton
        Class<?> singleton = Class.forName("android.util.Singleton");
        Field mInstanceField = singleton.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        // retrieve the raw IActivityManager instance
        Object rawIActivityManager = mInstanceField.get(gDefault);

        // create a proxy of IActivityManager, replace the raw IActivityManager instance
        Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{iActivityManagerInterface}, new IActivityManagerHandler(rawIActivityManager));

        // apply the proxy
        mInstanceField.set(gDefault, proxy);
    }
}
