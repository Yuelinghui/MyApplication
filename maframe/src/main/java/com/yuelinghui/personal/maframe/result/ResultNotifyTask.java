package com.yuelinghui.personal.maframe.result;

import android.content.Context;

/**
 * Created by yuelinghui on 16/8/30.
 */
public abstract class ResultNotifyTask {


    protected ResultNotifier<?> mResultNotifier = null;

    public ResultNotifyTask(ResultNotifier<?> notifier) {
        mResultNotifier = notifier;
    }

    /**
     * 任务执行
     */
    public void execute(Context context) {
        if (mResultNotifier != null && !mResultNotifier.prepare(context)) {
            mResultNotifier.notifyCancel();
        } else {
            onExecute();
        }
    }

    /**
     * 任务执行
     *
     */
    protected abstract void onExecute();
}
