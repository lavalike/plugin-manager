package com.wangzhen.plugin.hook;

import android.app.Instrumentation;
import android.os.Build;
import android.os.Handler;

import com.wangzhen.plugin.provider.ContextProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * ams hook helper.
 * Created by wangzhen on 2020/4/18.
 */
public class HookHelper {
    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";
    public static final String STUB_CLASS = "com.wangzhen.plugin.StubActivity";

    public static void hook() {
        try {
            hookActivityManagerNative();
            hookSystemHandler();
            if (Build.VERSION.SDK_INT >= 29) {
                hookInstrumentation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hookInstrumentation() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Field activityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        activityThreadField.setAccessible(true);
        //获取ActivityThread对象sCurrentActivityThread
        Object activityThread = activityThreadField.get(null);

        Field instrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
        instrumentationField.setAccessible(true);
        //从sCurrentActivityThread中获取成员变量mInstrumentation
        Instrumentation instrumentation = (Instrumentation) instrumentationField.get(activityThread);
        //创建代理对象InstrumentationProxy
        InstrumentationProxy proxy = new InstrumentationProxy(instrumentation, ContextProvider.sContext.getPackageManager());
        //将sCurrentActivityThread中成员变量mInstrumentation替换成代理类InstrumentationProxy
        instrumentationField.set(activityThread, proxy);
    }

    private static void hookActivityManagerNative() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
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

    private static void hookSystemHandler() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        //获取主线程对象
        Object activityThread = currentActivityThreadMethod.invoke(null);
        //获取mH字段
        Field mH = activityThreadClass.getDeclaredField("mH");
        mH.setAccessible(true);
        //获取Handler
        Handler handler = (Handler) mH.get(activityThread);
        //获取原始的mCallBack字段
        Field mCallBack = Handler.class.getDeclaredField("mCallback");
        mCallBack.setAccessible(true);
        //这里设置了我们自己实现了接口的CallBack对象
        mCallBack.set(handler, new ActivityThreadHandlerCallback(handler));
    }

    public static void patchClassLoader(ClassLoader cl, File apkFile, File optDexFile)
            throws IllegalAccessException, NoSuchMethodException, IOException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        // retrieve pathList from BaseDexClassLoader
        Field pathListField = DexClassLoader.class.getSuperclass().getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathListObj = pathListField.get(cl);

        // retrieve array "dexElements" from pathList
        Field dexElementArray = pathListObj.getClass().getDeclaredField("dexElements");
        dexElementArray.setAccessible(true);
        Object[] dexElements = (Object[]) dexElementArray.get(pathListObj);

        Class<?> elementClass = dexElements.getClass().getComponentType();

        // create an array, replace the raw array
        Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);

        Object object;
        // version compat.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // API 17 ~ 26 -> Element(File file, boolean isDirectory, File zip, DexFile dexFile)
                Constructor<?> constructor = elementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class);
                object = constructor.newInstance(apkFile, false, apkFile, DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0));
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // API == 17 and lower -> Element(File file, File zip, DexFile dexFile)
                Constructor<?> constructor = elementClass.getConstructor(File.class, File.class, DexFile.class);
                object = constructor.newInstance(apkFile, apkFile, DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0));
            } else {
                // API < 17 -> Element(File file, ZipFile zipFile, DexFile dexFile)
                // to be tested
                Constructor<?> constructor = elementClass.getConstructor(File.class, ZipFile.class, DexFile.class);
                object = constructor.newInstance(apkFile, new ZipFile(apkFile), DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0));
            }
        } else {
            // API 26 ~ -> Element(DexFile dexFile, File dexZipPath)
            Constructor<?> constructor = elementClass.getConstructor(DexFile.class, File.class);
            object = constructor.newInstance(DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0), apkFile);
        }

        Object[] toAddElementArray = new Object[]{object};
        System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
        System.arraycopy(toAddElementArray, 0, newElements, dexElements.length, toAddElementArray.length);
        dexElementArray.set(pathListObj, newElements);
    }
}
