package com.yuelinghui.personal.maframe.concurrent;

/**
 * Created by yuelinghui on 16/8/30.
 */
public interface Terminatable {
    /**
     * 是否停止
     *
     * @return
     */
    public boolean toBeTerminated();

    /**
     * 停止
     *
     * @return
     */
    public void terminate(Object tag);

    /**
     * 是否已停止
     *
     * @return
     */
    public boolean isTerminated();

    /**
     * 记录已停止
     *
     * @return
     */
    public void terminated();
}
