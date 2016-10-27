package com.yuelinghui.personal.maframe.step;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class StepLine {

    /**
     * 重复步骤最小间隔定义(ms)
     */
    private static final int MIN_REPEAT_INTERVAL = 500;
    /**
     * 最后一次步骤的标识/时间戳(ns)
     */
    private String mLastStep = null;
    private long mLastStepTimeStamp = 0;

    /**
     * stepline创建器
     *
     * @author liuzhiyun
     *
     */
    private static class StepHolder {
        public static StepLine instance = new StepLine();
    }

    public static StepLine getInstance() {
        return StepHolder.instance;
    }

    private StepLine() {
    }

    /**
     * 向步骤轴添加一个步骤
     *
     * @return true - 添加步骤成功并记录时间; false - 由于重复等原因添加失败
     */
    public synchronized boolean add(String step) {
        long currTime = System.nanoTime();

        if (!TextUtils.isEmpty(mLastStep) && mLastStep.equals(step)) {
            if (currTime - mLastStepTimeStamp <= TimeUnit.MILLISECONDS
                    .toNanos(MIN_REPEAT_INTERVAL)) {
                return false;
            }
        }

        mLastStep = step;
        mLastStepTimeStamp = currTime;

        return true;
    }
}
