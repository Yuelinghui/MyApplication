package com.yuelinghui.personal.maframe.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class SharedPreferencesUtil {


    public static final String MYINFO = "MYINFO";

    private SharedPreferences mDiskStorage;

    public SharedPreferencesUtil(Context c) {
        mDiskStorage = c.getSharedPreferences(MYINFO, 0);
    }

    /**
     * 在独立存储中读取类
     *
     * @param clazz
     * @return
     */
    public <T> T get(Class<T> clazz) {
        try {
            String value = mDiskStorage.getString(clazz.getName(), null);
            return JsonUtil.jsonToObject(value, clazz);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 将类保存到独立存储中
     *
     * @param clazz
     * @return
     */
    public <T> void set(T obj, Class<T> clazz) {
        String value = JsonUtil.objectToJson(obj);
        mDiskStorage.edit().putString(clazz.getName(), value).commit();
    }
}
