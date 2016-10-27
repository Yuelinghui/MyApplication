package com.yuelinghui.personal.widget.core;

import android.os.Environment;
import android.text.TextUtils;

import com.yuelinghui.personal.maframe.util.FileHelper;

import java.io.File;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class FilePathProvider {


    /**
     * 图片文件夹
     */
    private final static String sImagePath = "Images";

    /**
     * 掌上知乎文件夹
     */
    private final static String sJDFolderPath = "PlamZH";

    /**
     * 获取内部路径， 路径结尾带分隔符
     *
     * @return /data/data/wallet/files/
     */
    public static String getInternalPath() {
        try {
            return RunningContext.sAppContext.getFilesDir().getPath()
                    + File.separator;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取保存module的路径
     *
     * @return /data/data/wallet/files/moduleFolder
     */
    public static String getModulePath(String moduleFolder) {

        try {
            String modulePath = RunningContext.sAppContext.getFilesDir()
                    .getPath() + File.separator + moduleFolder + File.separator;
            if (!FileHelper.createFolder(modulePath)) {
                return "";
            }
            return modulePath;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取SD卡Android应用程序目录，若SD卡不存在，返回内部内存根目录，路径结尾带分隔符
     *
     * @return /storage/emulated/0/Android/data/wallet/files/
     */
    public static String getAndroidPath() {
        if (!isSDCardExist()) {
            // SD卡不存在，读取内部路径
            return getInternalPath();
        }
        try {
            return RunningContext.sAppContext.getExternalFilesDir(null)
                    .getPath() + File.separator;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取SD卡Pdf目录，若SD卡不存在，返回内部内存根目录下download目录，路径结尾带分隔符
     *
     * @return /storage/emulated/0/JDWallet/download/
     */
    public static String getAndroidDownloadPath() {

        String pdfPath = getAppPath() + "download";
        if (!FileHelper.isExist(pdfPath)) {
            FileHelper.createFolder(pdfPath);
        }
        return pdfPath + File.separator;
    }

    /**
     * 获取SD卡根目录下的应用程序文件夹，若SD卡不存在，返回内存卡根目录，路径结尾带分隔符
     *
     * @return /storage/emulated/0/JDWallet/ 获取失败返回""
     */
    public static String getAppPath() {
        if (!isSDCardExist()) {
            // SD卡不存在，读取内部路径
            return getInternalPath();
        }
        try {
            String appPath = Environment.getExternalStorageDirectory()
                    .getPath()
                    + File.separator
                    + sJDFolderPath
                    + File.separator;
            if (!FileHelper.createFolder(appPath)) {
                return "";
            }
            return appPath;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAppDownPath() {
        if (!isSDCardExist()) {
            // SD卡不存在，读取内部路径
            return getInternalPath();
        }
        try {
            String appPath = Environment.getExternalStorageDirectory()
                    .getPath()
                    + File.separator
                    + sJDFolderPath
                    + File.separator + "app" + File.separator;
            if (!FileHelper.createFolder(appPath)) {
                return "";
            }
            return appPath;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取保存图片的路径，路径结尾带分隔符
     *
     * @return /storage/emulated/0/JDWallet/Images/ 获取失败返回""
     */
    public static String getAppImageFolderPath() {
        String sdCardRootPath = getAppPath();
        if (TextUtils.isEmpty(sdCardRootPath)) {
            return "";
        }
        String fullPath = getAppPath() + sImagePath + File.separator;
        if (!FileHelper.createFolder(fullPath)) {
            return "";
        }
        return fullPath;
    }

    /**
     * 获取保存图片的路径，路径结尾带分隔符
     *
     * @return /data/data/wallet/files/Images/ 获取失败返回""
     */
    public static String getInternalImageFolderPath() {
        String internalPath = getInternalPath();
        if (TextUtils.isEmpty(internalPath)) {
            return "";
        }
        String fullPath = internalPath + sImagePath + File.separator;
        if (!FileHelper.createFolder(fullPath)) {
            return "";
        }
        return fullPath;
    }

    /**
     * SD卡是否存在 FIXME 放在这里不太合适
     *
     * @return
     */
    private static boolean isSDCardExist() {
        String SDCardState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(SDCardState);
    }

    /**
     * 获取保存图片的路径，路径结尾带分隔符
     *
     * @return /storage/emulated/0/JDWallet/Images/ocr/ ocr扫描卡片""
     */
    public static String getOcrImageFolderPath(String ocrFolder) {
        String sdCardRootPath = getAppPath();
        if (TextUtils.isEmpty(sdCardRootPath)) {
            return "";
        }
        String fullPath = sdCardRootPath + sImagePath + ocrFolder;
        if (!FileHelper.createFolder(fullPath)) {
            return "";
        }
        return fullPath;
    }
}
