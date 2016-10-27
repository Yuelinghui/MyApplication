package com.yuelinghui.personal.maframe.result;

/**
 * Created by yuelinghui on 16/8/30.
 * Result中断监听器
 */
public interface OnResultInterruptListener {

    /**
     * 中断
     *
     * @param resultCode 错误码
     * @param message    错误信息
     */
    public void onResultInterrupt(int resultCode, String message);
}
