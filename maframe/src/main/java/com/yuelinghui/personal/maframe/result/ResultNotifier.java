package com.yuelinghui.personal.maframe.result;

import android.content.Context;

/**
 * Created by yuelinghui on 16/8/30.
 */
public interface ResultNotifier<DataType> {


    /**
     * 进度控制，通知之前的准备工作
     *
     * @param context
     * @return true : 可以通知, false : 取消通知后续步骤
     */
    public boolean prepare(Context context);

    /**
     * 成功
     *
     * @param data    返回的实体对象
     * @param message 提示信息
     */
    public void notifySuccess(DataType data, String message);

    /**
     * 错误,做错误处理
     *
     * @param resultCode 错误码
     * @param message    错误信息
     */
    public void notifyFailure(int resultCode, String message);

    /**
     * 中断,做错误处理
     *
     * @param resultCode 错误码
     * @param message    错误信息
     */
    public void notifyInterrupt(int resultCode, String message);

    /**
     * 需要sms输入
     *
     * @param data    返回的实体对象
     * @param message 提示信息
     */
    public void notifySMS(DataType data, String message);

    /**
     * 敬请等待
     *
     * @param message 提示信息
     */
    public void notifyWaiting(String message);

    /**
     * 校验错误
     *
     * @param message
     */
    public void notifyVerifyFailure(String message);

    /**
     * 取消
     */
    public void notifyCancel();

    /**
     * 结束
     */
    public void notifyFinish();
}
