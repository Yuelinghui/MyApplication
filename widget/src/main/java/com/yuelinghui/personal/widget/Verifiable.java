package com.yuelinghui.personal.widget;

import java.util.Observer;

/**
 * Created by yuelinghui on 16/10/11.
 */

public interface Verifiable {
    /**
     * 校验
     *
     * @return
     */
    boolean verify();

    /**
     * 是否为空白
     *
     * @return
     */
    boolean isBlank();

    /**
     * 添加verify接口的观察者
     *
     * @param obj
     */
    void addObserver(Observer obj);
}
