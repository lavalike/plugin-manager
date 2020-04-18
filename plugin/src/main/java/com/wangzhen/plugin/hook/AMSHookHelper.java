package com.wangzhen.plugin.hook;

import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * AMSHookHelper
 * Created by wangzhen on 2020/4/18.
 */
public class AMSHookHelper {
    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";

    public static void hookActivityManagerNative() throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, NoSuchFieldException {

        Object gDefault;
        // 版本兼容
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
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

        // gDefault是一个 android.util.Singleton对象; 我们取出这个单例里面的字段
        Class<?> singleton = Class.forName("android.util.Singleton");
        Field mInstanceField = singleton.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        // ActivityManagerNative 的gDefault对象里面原始的 IActivityManager对象
        Object rawIActivityManager = mInstanceField.get(gDefault);

        // 创建一个这个对象的代理对象, 然后替换这个字段, 让我们的代理对象帮忙干活
        Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{iActivityManagerInterface}, new IActivityManagerHandler(rawIActivityManager));
        mInstanceField.set(gDefault, proxy);
    }
}
