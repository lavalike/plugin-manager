package com.wangzhen.plugin.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * SoLibManager
 * Created by wangzhen on 2020/5/23.
 */
public class SoLibManager {
    private static final String TAG = SoLibManager.class.getSimpleName();

    /**
     * So File executor
     */
    private ExecutorService mSoExecutor = Executors.newCachedThreadPool();
    /**
     * single instance of the SoLoader
     */
    private static SoLibManager sInstance = new SoLibManager();
    /**
     * app's lib dir
     */
    private static String sNativeLibDir = "";

    private SoLibManager() {
    }

    public static SoLibManager get() {
        return sInstance;
    }

    /**
     * get cpu name, according cpu type parse relevant so lib
     *
     * @return ARM、ARMV7、X86、MIPS
     */
    private String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            br.close();
            String[] array = text.split(":\\s+", 2);
            if (array.length >= 2) {
                return array[1];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressLint("DefaultLocale")
    private String getCpuArch(String cpuName) {
        String cpuArchitect = "armeabi";
        if (cpuName.toLowerCase().contains("arm")) {
            cpuArchitect = "armeabi";
        } else if (cpuName.toLowerCase().contains("x86")) {
            cpuArchitect = "x86";
        } else if (cpuName.toLowerCase().contains("mips")) {
            cpuArchitect = "mips";
        }

        return cpuArchitect;
    }

    /**
     * copy so lib to specify directory(/data/data/host_pack_name/pluginlib)
     *
     * @param dexPath      plugin path
     * @param nativeLibDir nativeLibDir
     */
    public void copyPluginSoLib(Context context, String dexPath, String nativeLibDir) {
        String cpuName = getCpuName();
        String cpuArchitect = getCpuArch(cpuName);

        sNativeLibDir = nativeLibDir;
        Log.d(TAG, "cpuArchitect: " + cpuArchitect);
        long start = System.currentTimeMillis();
        try {
            ZipFile zipFile = new ZipFile(dexPath);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory()) {
                    continue;
                }
                String zipEntryName = zipEntry.getName();
                if (zipEntryName.endsWith(".so") && zipEntryName.contains(cpuArchitect)) {
                    mSoExecutor.execute(new CopySoTask(context, zipFile, zipEntry));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        Log.d(TAG, "### copy so time : " + (end - start) + " ms");
    }

    /**
     * @author mrsimple
     */
    private static class CopySoTask implements Runnable {

        private String mSoFileName;
        private ZipFile mZipFile;
        private ZipEntry mZipEntry;
        private Context mContext;

        CopySoTask(Context context, ZipFile zipFile, ZipEntry zipEntry) {
            mZipFile = zipFile;
            mContext = context;
            mZipEntry = zipEntry;
            mSoFileName = parseSoFileName(zipEntry.getName());
        }

        private final String parseSoFileName(String zipEntryName) {
            return zipEntryName.substring(zipEntryName.lastIndexOf("/") + 1);
        }

        private void writeSoFile2LibDir() throws IOException {
            InputStream is = null;
            FileOutputStream fos = null;
            is = mZipFile.getInputStream(mZipEntry);
            File file = new File(sNativeLibDir, mSoFileName);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            copy(is, fos);
//            mZipFile.close();
        }

        /**
         * 输入输出流拷贝
         *
         * @param is
         * @param os
         */
        public void copy(InputStream is, OutputStream os) throws IOException {
            if (is == null || os == null)
                return;
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            int size = getAvailableSize(bis);
            byte[] buf = new byte[size];
            int i = 0;
            while ((i = bis.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, i);
            }
            bos.flush();
            bos.close();
            bis.close();
        }

        private int getAvailableSize(InputStream is) throws IOException {
            if (is == null)
                return 0;
            int available = is.available();
            return available <= 0 ? 1024 : available;
        }

        @Override
        public void run() {
            try {
                writeSoFile2LibDir();
                Log.d(TAG, "copy so lib success: " + mZipEntry.getName());
            } catch (IOException e) {
                Log.e(TAG, "copy so lib failed: " + e.toString());
                e.printStackTrace();
            }

        }

    }
}
