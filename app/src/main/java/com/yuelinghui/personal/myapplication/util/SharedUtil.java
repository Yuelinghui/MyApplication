package com.yuelinghui.personal.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.yuelinghui.personal.maframe.util.JsonUtil;

/**
 * Created by yuelinghui on 16/9/20.
 */
public class SharedUtil {

    public static final String OBJECT_INFO = "OBJECTINFO";

    private SharedPreferences mShared;

    public SharedUtil(Context context) {
        mShared = context.getSharedPreferences(OBJECT_INFO, 0);
    }

    public <T> T get(Class<T> clazz) {
        return get(clazz, clazz.getName());
    }

    public <T> T get(Class<T> clazz, String key) {
        try {
            String value = mShared.getString(key, null);
            return JsonUtil.jsonToObject(value, clazz);
        } catch (Exception e) {
        }
        return null;
    }

    public <T> void set(T obj, String key) {
        String value = JsonUtil.objectToJson(obj);
        mShared.edit().putString(key, value).commit();
    }

    public <T> void set(T obj, Class<T> clazz) {
        set(obj, clazz.getName());
    }
}
