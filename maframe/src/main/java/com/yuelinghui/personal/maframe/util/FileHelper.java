package com.yuelinghui.personal.maframe.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.yuelinghui.personal.maframe.exception.ThreadUncaughtExceptionHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class FileHelper {

    /**
     * sd卡是否存在
     *
     * @return
     */
    public static boolean isSDCardExist() {
        String sdcardState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(sdcardState);
    }

    /**
     * 文件或者文件夹是否存在
     *
     * @param path
     * @return
     */
    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static boolean createFolder(String floderPath) {
        if (TextUtils.isEmpty(floderPath)) {
            return false;
        }

        File floder = new File(floderPath);
        if (floder.exists()) {
            if (isFloderReadable(floderPath)) {
                // 路径存在并且可以读取，不用重新创建
                return true;
            }
            // 路径存在，但是不能读取，删除路径
            floder.delete();
        }
        return floder.mkdirs();
    }

    /**
     * 在指定路径下创建文件
     * 若路径不存在，先创建路径
     *
     * @param path
     * @param fileName
     * @return
     */
    public static File createFile(String path, String fileName) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(fileName)) {
            return null;
        }
        if (!createFolder(path)) {
            return null;
        }
        File file = new File(path + File.separator + fileName);
        try {
            file.createNewFile();
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 复制Assets中的文件到指定地址
     *
     * @param context
     * @param assetFile
     * @param destPath
     * @param destFile
     * @return
     */
    public static String copyAssetFile(Context context, String assetFile, String destPath, String destFile) {

        if (context == null || TextUtils.isEmpty(assetFile)
                || TextUtils.isEmpty(destPath) || TextUtils.isEmpty(destFile)) {
            return null;
        }
        boolean result = false;
        InputStream asset = null;
        File dest = null;
        OutputStream output = null;
        try {
            if (isExist(destFile)) {
                result = true;
            } else {
                asset = context.getAssets().open(assetFile);
                if (asset != null) {
                    dest = createFile(destPath, destFile);
                    if (dest != null) {
                        output = new FileOutputStream(dest);
                        if (output != null) {
                            int readLen = 0;
                            byte[] buf = new byte[1024];
                            while ((readLen = asset.read(buf)) != -1) {
                                output.write(buf, 0, readLen);
                            }
                            result = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            new ThreadUncaughtExceptionHandler().uncaughtException(e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
            }
            try {
                if (asset != null) {
                    asset.close();
                }
            } catch (Exception e) {
            }
            if (!result && dest != null) {
                dest.delete();
            }
        }
        return (result && dest != null) ? dest.getPath() : null;
    }

    /**
     * 拷贝文件
     *
     * @param srcFilePath
     * @param destFilePath
     * @return
     * @throws IOException
     */
    public static boolean copyFileTo(String srcFilePath, String destFilePath) throws IOException {
        if (TextUtils.isEmpty(srcFilePath)
                || TextUtils.isEmpty(destFilePath)) {
            return false;
        }
        File srcFile = new File(srcFilePath);
        File destFile = new File(destFilePath);
        if (srcFile.isDirectory() || destFile.isDirectory()) {
            return false;// 判断是否是文件
        }

        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = fis.read(buf)) != -1) {
            fos.write(buf, 0, readLen);
        }
        fos.flush();
        fos.close();
        fos = null;
        fis.close();
        fis = null;
        return true;
    }

    /**
     * 获取手机内存可用空间
     *
     * @param context
     * @return
     */
    public static long getFreeSpace(Context context) {
        if (context == null) {
            return 0;
        }
        File fileDir = context.getFilesDir();
        if (fileDir == null) {
            return 0;
        }
        StatFs statFs = new StatFs(fileDir.getPath());
        return statFs.getAvailableBlocks() * statFs.getBlockSize();
    }

    /**
     * 清空目录下所有文件和文件夹
     */
    public static void deleteDir(String rootPath) {
        if (TextUtils.isEmpty(rootPath)) {
            return;
        }
        File file = new File(rootPath);
        if (file == null || !file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                deleteFolderIncludeSelf(files[i]);
            }
        }
    }

    /**
     * 删除文件和文件夹
     *
     * @param dir
     */
    private static void deleteFolderIncludeSelf(File dir) {
        if (dir == null || !dir.exists())
            return;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files)
                if (file.isDirectory())
                    deleteFolderIncludeSelf(file);
                else
                    file.delete();
            dir.delete();
        } else
            dir.delete();
    }

    /**
     * 文件夹是否可以读取
     *
     * @param floderPath
     * @return
     */
    private static boolean isFloderReadable(String floderPath) {
        File tmpFile = new File(floderPath + "tmp.txt");
        FileOutputStream tmpStream = null;
        try {
            tmpStream = new FileOutputStream(tmpFile);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (tmpStream != null) {
                try {
                    tmpStream.close();
                } catch (Exception e) {
                }
            }
            if (tmpFile != null) {
                try {
                    tmpFile.delete();
                } catch (Exception e) {
                }
            }
        }
    }

    public boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        }
        return file.delete();
    }
}
