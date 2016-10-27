package com.yuelinghui.personal.maframe.concurrent;

/**
 * Created by yuelinghui on 16/8/30.
 * 通知器接口定义
 */
public interface Notifier<T> {
    /**
     * 通知之前做的准备工作
     *
     * @return
     */
    public boolean prepare();

    /**
     * 通知数据
     *
     * @param data
     */
    public void notify(T data);

    /**
     * 通知进度
     *
     * @param step  当前的步数[0,count]
     * @param count 总数
     */
    public void notifyProgress(int step, int count);
}
