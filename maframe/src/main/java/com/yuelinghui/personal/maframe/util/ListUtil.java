package com.yuelinghui.personal.maframe.util;

import java.util.Collection;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class ListUtil {


    /**
     * 判断一个集合是否为空
     *
     * @param collection
     * @return
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        if (collection == null || collection.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个集合是否不为空
     *
     * @param collection
     * @return
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    /**
     * 返回一个集合的size
     *
     * @return
     */
    public static <T> int size(Collection<T> collection) {
        if (isEmpty(collection)) {
            return 0;
        }
        return collection.size();
    }
}
