package com.yuelinghui.personal.widget;

/**
 * Created by yuelinghui on 16/10/13.
 */

public interface Attachable {
    /**
     * 当object依附在目标对象上时触发。
     *
     * @param obj
     */
    void attach(Object obj);

    /**
     * 当object取消依附在目标对象上时触发。
     *
     * @param obj
     */
    void detach(Object obj);
}
