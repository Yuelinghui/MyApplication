package com.yuelinghui.personal.maframe.concurrent;

import android.os.Process;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class LoopThread extends Thread implements Terminatable {

    /**
     * Millis since epoch when alarm should stop;
     */
    private final long mMillisInFuture;
    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;
    private LoopTask mTask = null;
    private SimpleController controller = new SimpleController();
    /**
     * 任务执行使用的指定时间间隔（ms）
     */
    private long[] mIntervals = null;
    /**
     * 计数器
     */
    private long mCount = 0;

    public LoopThread(long millisInFuture, long countDownInterval, long... intervals) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
        mIntervals = intervals != null ? intervals.clone() : null;
        controller.start();
    }

    public boolean start(LoopTask task) {
        if (task == null) {
            return false;
        }
        if (mMillisInFuture <= 0) {
            return false;
        }
        if (toBeTerminated() || isTerminated()) {
            return false;
        }
        this.mTask = task;
        start();
        return true;
    }

    public boolean isIdle() {
        return !toBeTerminated() && !isTerminated();
    }

    @Override
    public void run() {
        // 设置优先级为后台，这样当多个线程并发后很多无关紧要的线程分配的CPU时间将会减少，有利于主线程的处理
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        int fixIntervalIndex = 0;
        int fixIntervalSize = mIntervals != null ? mIntervals.length : 0;
        try {
            while (!toBeTerminated() && mCount <= mMillisInFuture) {
                if (fixIntervalIndex < fixIntervalSize) {
                    Thread.sleep(mIntervals[fixIntervalIndex]);
                    mCount += mIntervals[fixIntervalIndex];
                } else {
                    Thread.sleep(mCountdownInterval);
                    mCount += mCountdownInterval;
                }

                if (toBeTerminated()) {
                    break;
                }
                if (!mTask.onExecute()) {
                    break;
                }
                fixIntervalIndex++;
            }

        } catch (Exception e) {
        }

        terminated();
        mTask = null;
    }

    @Override
    public boolean toBeTerminated() {
        return controller.toBeStopped();
    }

    @Override
    public void terminate(Object tag) {
        controller.stop();
    }

    @Override
    public boolean isTerminated() {
        return controller.isStopped();
    }

    @Override
    public void terminated() {
        // wait for all active blocks terminated.
        controller.stopped();
    }


    /**
     * 循环任务
     */
    public static interface LoopTask {

        /**
         * 任务执行
         *
         * @return true : 执行成功, false : 退出loop
         */
        boolean onExecute();
    }
}
