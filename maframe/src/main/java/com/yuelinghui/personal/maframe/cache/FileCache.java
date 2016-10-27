package com.yuelinghui.personal.maframe.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;
import com.yuelinghui.personal.maframe.concurrent.Callbackable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class FileCache {

    /**
     * 后缀名
     */
    private static final String WHOLESALE_CONV = "";
    /**
     * 存储空间预定大小
     */
    private static final int CACHE_SIZE = 10;
    /**
     * 剩余存储空间预定大小
     */
    private static final int FREE_CACHE_SIZE = 10;

    private static final int MB = 1024 * 1024;

    /**
     * 对象互斥锁
     */
    private static byte[] sObjectLock = new byte[0];

    /**
     * 图片存储路径
     */
    private String mImageDir = null;
    /**
     * 序列化类存储路径
     */
    private String mObjectDir = null;

    public FileCache(String path) {
        mImageDir = path + "/image";
        mObjectDir = path + "/object";
        removeImageCache(mImageDir);
    }

    /**
     * 从缓存中获取图片
     *
     * @param url
     * @return
     */
    public Bitmap getImage(String url) {
        // 图片的路径
        String path = mImageDir + "/" + convertUrlToFileName(url);
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path);
            if (bitmap == null) {
                // 没有图片删除路径
                file.delete();
            } else {
                // 更新文件修改时间
                updateFileTime(path);
            }
        } catch (Exception e) {
            file.delete();
        }

        return bitmap;
    }

    /**
     * 异步获取图片
     *
     * @param url
     * @param localCallbackable
     */
    public void getImageAsync(final String url, final Callbackable<Bitmap> localCallbackable) {
        RunningEnvironment.threadPool().execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getImage(url);
                if (bitmap != null) {
                    localCallbackable.callback(bitmap);
                }
            }
        });
    }

    /**
     * 将图片存入文件缓存
     *
     * @param url
     * @param bitmap
     */
    public void setImage(String url, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        File dirFile = new File(mImageDir);
        if (!dirFile.exists()) {
            // 创建文件夹
            dirFile.mkdir();
        }

        // 判断原有文件是否存在，如果存在就忽略修改
        String path = mImageDir + "/" + convertUrlToFileName(url);
        File file = new File(path);
        if (file.exists()) {
            if (!file.delete()) {
                return;
            }
        }

        // 创建临时文件，写完之后切换为指定文件
        File tmpFile = new File(path + ".tmp");
        if (tmpFile.exists()) {
            if (!tmpFile.delete()) {
                return;
            }
        }

        try {
            tmpFile.createNewFile();
        } catch (IOException e) {
            tmpFile.delete();
            return;
        }

        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(tmpFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

            tmpFile.renameTo(file);

        } catch (Exception e) {
            tmpFile.delete();
        } finally {
            if (outStream != null) {
                try {
                    outStream.flush();
                } catch (IOException e) {
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 异步存储图像数据
     *
     * @param url
     * @param bitmap
     */
    public void setImageAsync(final String url, final Bitmap bitmap) {
        RunningEnvironment.threadPool().execute(new Runnable() {
            @Override
            public void run() {
                setImage(url, bitmap);
            }
        });
    }

    /**
     * 读取对象信息
     *
     * @param fileName
     * @return
     */
    public Object getObject(String fileName) {
        synchronized (sObjectLock) {
            Object obj = null;

            // 文件位置
            fileName = mObjectDir + "/" + convertUrlToFileName(fileName);

            FileInputStream fIn = null;
            ObjectInputStream oIn = null;
            try {
                fIn = new FileInputStream(fileName);
                oIn = new ObjectInputStream(fIn);
                obj = oIn.readObject();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
                new File(fileName).delete();
            } finally {
                if (oIn != null) {
                    try {
                        oIn.close();
                    } catch (Exception e) {
                    }
                }
                if (fIn != null) {
                    try {
                        fIn.close();
                    } catch (Exception e) {
                    }
                }
            }

            return obj;
        }
    }

    /**
     * 异步返回数据文件数据
     */
    public void getObjectAsync(final String fileName,
                               final Callbackable<Object> localCallbackable) {
        RunningEnvironment.threadPool().execute(new Runnable() {

            @Override
            public void run() {
                Object object = getObject(fileName);
                localCallbackable.callback(object);
            }
        });
    }

    /**
     * 存储对象信息
     *
     * @param fileName
     * @param obj
     */
    public void setObject(String fileName, Object obj) {
        if (obj == null) {
            return;
        }

        // 创建根目录
        File dirFile = new File(mObjectDir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        // 忽略原始文件是否存在，创建临时文件，写完之后再重命名为原始文件
        String path = mObjectDir + "/" + convertUrlToFileName(fileName);

        // 创建临时文件，写完之后切换为指定文件
        File tmpFile = new File(path + ".tmp");
        if (tmpFile.exists()) {
            if (!tmpFile.delete()) {
                return;
            }
        }

        try {
            tmpFile.createNewFile();
        } catch (IOException e) {
            tmpFile.delete();
            return;
        }

        ObjectOutputStream out = null;
        FileOutputStream fOut = null;
        try {
            // 写入文件
            fOut = new FileOutputStream(tmpFile);
            out = new ObjectOutputStream(fOut);
            out.writeObject(obj);
            out.flush();
            out.close();
            fOut.flush();
            fOut.close();

            synchronized (sObjectLock) {
                File file = new File(path);
                if (file.exists()) {
                    if (!file.delete()) {
                        tmpFile.delete();
                        return;
                    }
                }
                tmpFile.renameTo(file);
            }
        } catch (Exception e) {
            tmpFile.delete();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (Exception e) {
                }
            }
            if (fOut != null) {
                try {
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                }
            }
        }

    }

    /**
     * 异步存储数据对象
     *
     * @param fileName
     * @param obj
     * @author wyqiuchunlong
     */
    public void setObjectAsync(final String fileName, final Object obj) {

        RunningEnvironment.threadPool().execute(new Runnable() {

            @Override
            public void run() {
                setObject(fileName, obj);
            }
        });
    }

    /**
     * 清除缓存
     * 计算存储目录下的文件大小，
     * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_CACHE_SIZE的规定
     * 那么删除40%最近没有被使用的文件
     *
     * @param dirPath
     * @return
     */
    private boolean removeImageCache(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return true;
        }

        int dirSize = 0;
        for (File file : files) {
            if (file.getName().contains(WHOLESALE_CONV)) {
                dirSize += file.length();
            }
        }

        if (dirSize > CACHE_SIZE || freeSpaceOnCache() < FREE_CACHE_SIZE) {
            // 删除文件的大小
            int removeFactor = (int) ((0.4 * files.length) + 1);
            // 把文件按照修改的时间来排序,修改的早的在前面
            Arrays.sort(files, new FileLastModifySort());
            // 删除文件
            for (int i = 0; i < removeFactor; i++) {
                if (files[i].getName().contains(WHOLESALE_CONV)) {
                    files[i].delete();
                }
            }
        }
        if (freeSpaceOnCache() < FREE_CACHE_SIZE) {
            // 删除之后剩余空间还是比规定的小
            return false;
        }
        return true;
    }

    /**
     * 计算剩余的空间
     *
     * @return
     */
    private int freeSpaceOnCache() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        /**
         * 获得可以使用的存储块数量和存储块大小（bytes）
         */
        double appFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        return (int) appFreeMB;
    }

    /**
     * 将url转成文件名
     */
    private String convertUrlToFileName(String url) {
        String[] strs = url.split("/");
        return strs[strs.length - 1] + WHOLESALE_CONV;
    }

    /**
     * 获取文件最后存储时间
     *
     * @author wyqiuchunlong
     * @param fileName
     * @return
     */
    public Date getObjectDate(String fileName) {
        // 文件位置
        fileName = mObjectDir + "/" + convertUrlToFileName(fileName);

        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        return new Date(file.lastModified());
    }

    /**
     * 修改文件的最后修改时间
     */
    private void updateFileTime(String path) {
        File file = new File(path);
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
    }

    private class FileLastModifySort implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            if (file1.lastModified() > file2.lastModified()) {
                return 1;
            } else if (file1.lastModified() == file2.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

}
