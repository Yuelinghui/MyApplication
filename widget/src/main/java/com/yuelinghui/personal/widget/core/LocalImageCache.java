package com.yuelinghui.personal.widget.core;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;
import com.yuelinghui.personal.maframe.cache.ImageMemoryCache;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class LocalImageCache extends ImageMemoryCache {


    /**
     * 图片缓存大小
     *
     * @return
     */
    private static int cacheSize() {
        // 初始化强引用缓存
        int memClass = ((ActivityManager) RunningEnvironment.sAppContext
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        return 1024 * 1024 * memClass / 4; // 硬引用缓存容量，为系统可用内存的1/4
    }

    /**
     * imagecache创建器
     *
     * @author liuzhiyun
     *
     */
    private static class CacheHolder {
        public static LocalImageCache instance = new LocalImageCache(
                LocalImageCache.cacheSize());
    }

    public static LocalImageCache getInstance() {
        return CacheHolder.instance;
    }

    private LocalImageCache(int cacheSize) {
        super(cacheSize);
    }

    /**
     * 保存图片，先存到内存，再存到SD卡，SD卡上的图片对用户可见，推荐使用。
     *
     * @param bitmap
     * @param imgName
     *            不传递图片扩展名
     * @return 图片保存在硬盘中的路径
     */
    public static String save(Bitmap bitmap, String imgName) {
        if (bitmap == null || TextUtils.isEmpty(imgName)) {
            return null;
        }
        getInstance().saveBitmap(imgName, bitmap);
        return saveBitmapToDisk(bitmap, imgName, true);
    }

    /**
     * 保存图片，先存到内存，再存到内部存储，内部存储上的图片对用户可见
     *
     * @param bitmap
     * @param imgName
     *            不传递图片扩展名
     * @return 图片保存在硬盘中的路径
     */
    public static String saveInternal(Bitmap bitmap, String imgName) {
        if (bitmap == null || TextUtils.isEmpty(imgName)) {
            return null;
        }
        getInstance().saveBitmap(imgName, bitmap);
        return saveBitmapToDisk(bitmap, imgName, false);
    }

    /**
     * 读取图片 首先在缓存中读取，没有则在SD中读取。
     *
     * @param imgName
     *            不用加扩展名
     * @return
     */
    public static Bitmap load(String imgName) {
        if (TextUtils.isEmpty(imgName)) {
            return null;
        }
        // 选读取内存
        Bitmap bitmap = getInstance().getBitmap(imgName);
        if (bitmap == null) {
            // 不存在则读取硬盘
            bitmap = getBitmapFromDisk(imgName, false);
        }
        return bitmap;
    }

    /**
     * 读取图片 首先在缓存中读取，没有则在内部存储中读取。
     *
     * @param imgName
     *            不用加扩展名
     * @return
     */
    public static Bitmap loadInternal(String imgName) {
        if (TextUtils.isEmpty(imgName)) {
            return null;
        }
        // 选读取内存
        Bitmap bitmap = getInstance().getBitmap(imgName);
        if (bitmap == null) {
            // 不存在则读取硬盘
            bitmap = getBitmapFromDisk(imgName, false);
        }
        return bitmap;
    }

    /**
     * 保存图片到硬盘
     *
     * @param bitmap
     * @param imgName
     * @param isSaveToSDCard
     * @return 硬盘路径
     */
    private static String saveBitmapToDisk(Bitmap bitmap, String imgName,
                                           boolean isSaveToSDCard) {
        if (bitmap == null || TextUtils.isEmpty(imgName)) {
            return null;
        }
        String imageFolderPath = null;
        if (isSaveToSDCard) {
            imageFolderPath = FilePathProvider.getAppImageFolderPath();
        } else {
            imageFolderPath = FilePathProvider.getInternalImageFolderPath();
        }
        File imageFile = new File(imageFolderPath + imgName + ".png");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imageFile);
            if (fileOutputStream != null && bitmap != null) {
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (Exception e) {
            imageFile.delete();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
            }
        }
        return imageFile.getPath();
    }

    /**
     * 在本地读取图片
     *
     * @param imgName
     * @param isLoadFromSDCard
     * @return
     */
    private static Bitmap getBitmapFromDisk(String imgName,
                                            boolean isLoadFromSDCard) {
        if (TextUtils.isEmpty(imgName)) {
            return null;
        }
        String imageFolderPath = null;
        if (isLoadFromSDCard) {
            imageFolderPath = FilePathProvider.getAppImageFolderPath();
        } else {
            imageFolderPath = FilePathProvider.getInternalImageFolderPath();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imageFolderPath + imgName
                + ".png");
        return bitmap;
    }
}
