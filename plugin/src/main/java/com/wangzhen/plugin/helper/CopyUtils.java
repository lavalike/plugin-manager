package com.wangzhen.plugin.helper;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * CopyUtils
 * Created by wangzhen on 2020/4/1.
 */
public class CopyUtils {
    /**
     * copy file from asset to a new path
     *
     * @param context context
     * @param oldPath old path
     * @param newPath new path
     * @return success
     */
    public static boolean copyAsset(Context context, String oldPath, String newPath) {
        InputStream stream = null;
        BufferedInputStream inStream = null;
        BufferedOutputStream outStream = null;
        try {
            inStream = new BufferedInputStream(stream = context.getAssets().open(oldPath));
            outStream = new BufferedOutputStream(new FileOutputStream(newPath));
            int BUFF_SIZE = 1024;
            byte[] buff = new byte[1024];
            int count;
            while ((count = inStream.read(buff, 0, BUFF_SIZE)) > 0) {
                outStream.write(buff, 0, count);
            }
            return true;
        } catch (Exception ignore) {
            ignore.printStackTrace();
        } finally {
            IOUtils.close(stream, inStream, outStream);
        }
        return false;
    }
}
