package com.yuelinghui.personal.maframe.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class ImageMemoryCache {
    /**
     * 软引用缓存容量
     */
    private static final int SOFT_CACHE_SIZE = 15;
    /**
     * 强引用缓存（使用LruCache）
     */
    private static LruCache<String, Bitmap> mLruCache = null;
    /**
     * 软引用缓存
     */
    private static LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache = null;

    public ImageMemoryCache(int cacheSize) {
        if (mLruCache == null) {
            mLruCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    if (value != null) {
                        return value.getRowBytes() * value.getHeight();
                    } else {
                        return 0;
                    }
                }

                @Override
                protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                    if (oldValue != null) {
                        // 硬引用缓存容量满的时候，会根据LRU算法把最近没有被使用的图片转入此软引用缓存
                        mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
                    }
                }
            };
        }

        if (mSoftCache == null) {
            // 默认按照插入顺序排序，第三个参数设置为true，指定按照访问顺序排序，访问的元素会移至链表尾部
            mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_SIZE, 0.75f, true) {

                private static final long serialVersionUID = 1L;

                @Override
                protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
                    // 插入元素时是否移除最老的元素
                    if (size() > SOFT_CACHE_SIZE) {
                        return true;
                    }
                    return false;
                }
            };
        }
    }

    /**
     * 获取图片
     *
     * @param url
     * @return
     */
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        // 先从硬引用缓存中获取
        synchronized (mLruCache) {
            bitmap = mLruCache.get(url);
        }
        if (bitmap != null) {
            return bitmap;
        }
        // 如果硬引用缓存中取不到，到软引用缓存里取
        synchronized (mSoftCache) {
            SoftReference<Bitmap> bitmapSoftReference = mSoftCache.get(url);
            if (bitmapSoftReference != null) {
                bitmap = bitmapSoftReference.get();
                if (bitmap != null) {
                    // 从软引用中获得了图片，再移回硬引用
                    mLruCache.put(url, bitmap);
                    mSoftCache.remove(url);
                    return bitmap;
                } else {
                    mSoftCache.remove(url);
                }
            }
        }
        return null;
    }

    /**
     * 添加图片到缓存
     *
     * @param url
     * @param bitmap
     */
    public void saveBitmap(String url, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        synchronized (mLruCache) {
            mLruCache.put(url, bitmap);
        }
    }

    public void clearCache() {
        mSoftCache.clear();
    }
}
