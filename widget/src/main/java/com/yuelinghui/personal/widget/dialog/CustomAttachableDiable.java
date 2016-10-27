package com.yuelinghui.personal.widget.dialog;

import android.app.Dialog;
import android.content.Context;

import com.yuelinghui.personal.widget.Attachable;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class CustomAttachableDiable extends Dialog {


    /**
     * 对话框依附监听器
     */
    protected Attachable mAttachable;

    /**
     * 构造函数
     * @param context
     * @param theme
     */
    public CustomAttachableDiable(Context context, int theme) {
        super(context, theme);
        // 设置默认依附事件
        if (context instanceof Attachable) {
            mAttachable = (Attachable) context;
        }
    }

    /**
     * 设置依附事件监听
     *
     * @param attachable
     */
    public void setAttachableListener(Attachable attachable) {
        mAttachable = attachable;
    }

    /**
     * Dialog显示时触发
     */
    public void show() {
        super.show();
        if (mAttachable != null) {
            mAttachable.attach(this);
        }
    }

    /**
     * Dialog关闭时触发
     */
    public void dismiss() {
        super.dismiss();
        if (mAttachable != null) {
            mAttachable.detach(this);
        }
    };

}
