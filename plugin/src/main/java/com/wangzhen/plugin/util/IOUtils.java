package com.wangzhen.plugin.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * IOUtils
 * Created by wangzhen on 2020/4/1.
 */
public class IOUtils {

    /**
     * close all streams
     *
     * @param closeables closeables
     */
    public static void close(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
