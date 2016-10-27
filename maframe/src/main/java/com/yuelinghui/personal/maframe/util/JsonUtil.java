package com.yuelinghui.personal.maframe.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class JsonUtil {


    /**
     * 将对象转成json
     *
     * @param obj
     * @return
     */
    public static <T> String objectToJson(T obj) {
        if (obj != null) {
            try {
                return new Gson().toJson(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 将list数据转成json
     *
     */
    public static <T> String listToJson(List<T> list) {
        if (!ListUtil.isEmpty(list)) {
            try {
                return new Gson().toJson(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 把json数据解析成对象
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        if (!TextUtils.isEmpty(json)) {
            try {
                return new Gson().fromJson(json, clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 把json数据解析成list对象
     *
     * @param json
     * @param type
     * @return
     */
    public static <T> List<T> jsonToList(String json, Type type) {
        if (!TextUtils.isEmpty(json)) {
            try {
                return new Gson().fromJson(json, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<T>();
    }
}
