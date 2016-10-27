package com.yuelinghui.personal.maframe.step;

import android.text.TextUtils;

import com.yuelinghui.personal.maframe.concurrent.CancelListener;

/**
 * Created by yuelinghui on 16/8/30.
 */
public abstract class StepTask implements CancelListener {


    /**
     * 步骤数量
     */
    private int mStepCount = 0;
    /**
     * 当前步骤(1为起始值)
     */
    private int mCurrStep = 1;
    /**
     * 在完成的时候需要执行下一个步骤
     */
    private boolean mExecuteNext = false;
    /**
     * 任务名称
     */
    private String mTaskName = null;

    /**
     * 任务开始
     *
     */
    protected abstract boolean onStart();

    /**
     * 任务完成
     *
     */
    protected abstract void onFinish();

    /**
     * 步骤1
     */
    protected abstract void onStep1Start();

    /**
     * 步骤2
     */
    protected abstract void onStep2Start();

    /**
     * 步骤3
     */
    protected void onStep3Start() {
    }

    /**
     * constructor
     *
     *            步骤数量
     */
    public StepTask() {
        init(2);
    }

    /**
     * constructor
     *
     * @param taskName
     *            任务名称
     */
    public StepTask(String taskName) {
        init(2);
        mTaskName = taskName;
    }

    /**
     * constructor
     *
     * @param stepCount
     *            步骤数量
     */
    public StepTask(int stepCount) {
        init(stepCount);
    }

    /**
     * constructor
     *
     * @param taskName
     *            任务名称
     * @param stepCount
     *            步骤数量
     */
    public StepTask(int stepCount, String taskName) {
        init(stepCount);
        mTaskName = taskName;
    }

    /**
     * 初始化方法
     *
     * @param stepCount
     */
    private void init(int stepCount) {
        if (stepCount > 3) {
            throw new IllegalArgumentException(
                    "The count of step must be less than or equal 3.");
        }
        mStepCount = stepCount;
        mCurrStep = 1;
        if (TextUtils.isEmpty(mTaskName)) {
            // 外部没有设置任务名，使用默认
            mTaskName = StepTask.class.getCanonicalName();
        }
    }

    /**
     * 下一个步骤
     */
    public void nextStep() {
        mExecuteNext = true;
    }

    /**
     * 步骤结束
     */
    public void finishStep() {

        if (!mExecuteNext) {
            finish();
            return;
        }

        if (mCurrStep >= mStepCount) {
            finish();
            return;
        }

        mCurrStep++;
        mExecuteNext = false;
        switch (mCurrStep) {
            case 1:
                onStep1Start();
                break;
            case 2:
                onStep2Start();
                break;
            case 3:
                onStep3Start();
                break;
            default:
                finish();
                break;
        }
    }

    /**
     * 开始执行
     */
    public void execute() {
        // 确认步骤不重复
        if (!StepLine.getInstance().add(mTaskName)) {
            return;
        }

        if (!onStart()) {
            finish();
            return;
        }
        onStep1Start();
    }

    /**
     * 取消处理
     */
    @Override
    public void onCancel(int cancelType) {
        finish();
    }

    /**
     * 结束所有步骤
     */
    private void finish() {
        // 清空步骤轴
        StepLine.getInstance().add("");
        onFinish();
    }
}
