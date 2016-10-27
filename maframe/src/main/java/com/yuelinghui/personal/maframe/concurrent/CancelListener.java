package com.yuelinghui.personal.maframe.concurrent;

/**
 * Created by yuelinghui on 16/8/30.
 */
public interface CancelListener {
    /**
     * 结果处理不涉及页面
     */
    public static final int CANCEL_NONE = 0;
    /**
     * 结果处理后不显示任何界面
     */
    public static final int CANCEL_EMPTY = 1;
    /**
     * 结果处理后显示错误界面
     */
    public static final int CANCEL_ERROR = 2;
    /**
     * 结果处理后退出
     */
    public static final int CANCEL_FINISH = 3;

    /**
     * 取消
     *
     * @param cancelType 参见上面参数
     */
    void onCancel(int cancelType);
}
