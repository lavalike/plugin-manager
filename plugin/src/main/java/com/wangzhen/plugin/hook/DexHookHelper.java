package com.wangzhen.plugin.hook;

import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * dex hook helper.
 * Created by wangzhen on 2020/4/18.
 */
public class DexHookHelper {
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
