package com.wangzhen.plugin.util;

import android.content.Context;
import android.text.TextUtils;
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
 * so library manager
 * Created by wangzhen on 2020/5/23.
 */
public class SoLibManager {
    private static final String TAG = SoLibManager.class.getSimpleName();
    private ExecutorService mSoExecutor = Executors.newCachedThreadPool();
    private static SoLibManager sInstance = new SoLibManager();
    private static String sNativeLibDir = "";

    private SoLibManager() {
    }

    public static SoLibManager get() {
        return sInstance;
    }

    /**
     * copy so lib to specify directory
     *
     * @param dexPath      plugin path
     * @param nativeLibDir nativeLibDir
     */
    public void copy(Context context, String dexPath, String nativeLibDir) {
        String cpuName = getCpuName();
        String cpuArchitect = getCpuArch(cpuName);
        sNativeLibDir = nativeLibDir;
        Log.e(TAG, "cpuArchitect: " + cpuArchitect);
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
                    final long lastModify = zipEntry.getTime();
                    if (lastModify == FileUtils.getSoLastModifiedTime(context, zipEntryName)) {
                        Log.e(TAG, "skip copying, the so lib already exists : " + zipEntryName);
                        continue;
                    }
                    mSoExecutor.execute(new CopySoTask(context, zipFile, zipEntry, lastModify));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * get cpu name
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

    /**
     * get cpu architecture by cpu name
     *
     * @param cpuName cpu name
     * @return cpu architecture
     */
    private String getCpuArch(String cpuName) {
        String cpuArchitect = CpuArch.CPU_ARMEABI;
        if (!TextUtils.isEmpty(cpuName)) {
            if (cpuName.toLowerCase().contains("arm")) {
                cpuArchitect = CpuArch.CPU_ARMEABI;
            } else if (cpuName.toLowerCase().contains("x86")) {
                cpuArchitect = CpuArch.CPU_X86;
            } else if (cpuName.toLowerCase().contains("mips")) {
                cpuArchitect = CpuArch.CPU_MIPS;
            }
        }
        return cpuArchitect;
    }

    /**
     * task to copy so library
     */
    private static class CopySoTask implements Runnable {

        private String mSoFileName;
        private ZipFile mZipFile;
        private ZipEntry mZipEntry;
        private Context mContext;
        private long mLastModifyTime;

        CopySoTask(Context context, ZipFile zipFile, ZipEntry zipEntry, long lastModify) {
            mZipFile = zipFile;
            mContext = context;
            mZipEntry = zipEntry;
            mSoFileName = parseSoFileName(zipEntry.getName());
            mLastModifyTime = lastModify;
        }

        String parseSoFileName(String zipEntryName) {
            return zipEntryName.substring(zipEntryName.lastIndexOf("/") + 1);
        }

        @Override
        public void run() {
            try {
                copy(mZipFile.getInputStream(mZipEntry), new FileOutputStream(new File(sNativeLibDir, mSoFileName)));
                FileUtils.setSoLastModifiedTime(mContext, mZipEntry.getName(), mLastModifyTime);
            } catch (IOException ignore) {
            }
        }

        /**
         * copy so
         *
         * @param is input stream
         * @param os file output stream
         */
        void copy(InputStream is, OutputStream os) throws IOException {
            if (is == null || os == null)
                return;
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            int size = getAvailableSize(bis);
            byte[] buf = new byte[size];
            int length;
            while ((length = bis.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, length);
            }
            bos.flush();
            bos.close();
            bis.close();
        }

        int getAvailableSize(InputStream is) throws IOException {
            if (is == null)
                return 0;
            int available = is.available();
            return available <= 0 ? 1024 : available;
        }

    }
}
