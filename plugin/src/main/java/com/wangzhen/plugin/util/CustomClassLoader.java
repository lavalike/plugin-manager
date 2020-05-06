package com.wangzhen.plugin.util;

import dalvik.system.DexClassLoader;

/**
 * CustomClassLoader
 * Created by wangzhen on 2020/4/21.
 */
public class CustomClassLoader extends DexClassLoader {
    public CustomClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }
}
