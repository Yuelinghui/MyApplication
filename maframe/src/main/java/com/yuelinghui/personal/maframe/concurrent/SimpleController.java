package com.yuelinghui.personal.maframe.concurrent;

/**
 * Created by yuelinghui on 16/8/30.
 * 通用线程进度控制器
 */
public class SimpleController {

    volatile private boolean stopped = true;
    volatile private boolean stop = false;
    private byte[] mLock = new byte[0];

    public SimpleController() {
    }

    /**
     * 开始
     */
    public void start() {
        stopped = false;
        stop = false;
    }

    /**
     * 停止
     */
    public void stop() {
        synchronized (mLock) {
            stop = true;
        }
    }

    /**
     * 已经停止
     */
    public void stopped() {
        stop = true;
        stopped = true;
    }

    /**
     * 是否要停止
     *
     * @return
     */
    public boolean toBeStopped() {
        return stop;
    }

    /**
     * 是否已经停止
     *
     * @return
     */
    public boolean isStopped() {
        return stop && stopped;
    }

    /**
     * 获取线程锁
     *
     * @return
     */
    public byte[] getLock() {
        return mLock;
    }
}
