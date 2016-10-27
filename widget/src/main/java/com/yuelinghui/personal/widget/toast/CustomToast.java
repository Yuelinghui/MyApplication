package com.yuelinghui.personal.widget.toast;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yuelinghui.personal.widget.Attachable;
import com.yuelinghui.personal.widget.R;
import com.yuelinghui.personal.widget.core.RunningContext;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class CustomToast extends Toast {


    /**
     * 当前展示的toast
     */
    private static CustomToast sShowingToast = null;
    /**
     * 提示长短时长的字符限制
     */
    static private final int CHAR_DURATION_LIMIT = 20;
    /**
     * 弹出提示文案
     */
    private CharSequence mToastStr = null;
    /**
     * context
     */
    private Context mContext;
    /**
     * 提示框依附监听器
     */
    protected Attachable mAttachable;

    /**
     * 构造函数
     *
     * @param context
     * @param text
     */
    private CustomToast(Context context, CharSequence text) {
        super(context);
        // 设置默认依附监听器
        if (context instanceof Attachable) {
            mAttachable = (Attachable) context;
        }
        mToastStr = text;
        mContext = context;
    }

    @Override
    public void show() {
        // 如果已经退出应用，则不弹出toast
        if (RunningContext.sAppData.sIsExitApp) {
            return;
        }

        if (TextUtils.isEmpty(mToastStr)) {
            return;
        }
        // 关闭之前展示的toast
        if (sShowingToast != null) {
            sShowingToast.cancel();
        }
        // 设置当前展示的toast
        sShowingToast = this;
        super.show();
        if (mAttachable != null) {
            mAttachable.attach(this);
        }

        if (mAttachable != null) {
            mAttachable.detach(this);
        }
    }

    @Override
    public void cancel() {
        // 当前toast关闭时设置当前展示的toast为空
        sShowingToast = null;
        super.cancel();
    }

    /**
     * 构造CPToast
     *
     * @param text
     * @return
     */
    public static CustomToast makeText(CharSequence text) {
        return makeText(RunningContext.sAppContext, text);
    }

    /**
     * 由外部传来的Context构造CPToast 主要针对从插件还未切换到主工程环境时（Dispatcher中使用）
     *
     * @param context
     * @param text
     * @return
     */
    public static CustomToast makeText(Context context, CharSequence text) {
        CustomToast toast = new CustomToast(context, text);
        LayoutInflater inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflate.inflate(R.layout.custom_toast, null);
        TextView tip = (TextView) view.findViewById(R.id.txt_tip);
        tip.setText(text);

        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        if (text == null || text.length() <= CHAR_DURATION_LIMIT) {
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast.setDuration(Toast.LENGTH_LONG);
        }
        return toast;
    }

    /**
     *
     * @param resId
     *            The resource id of the string resource to use. Can be
     *            formatted text.
     * @return
     */
    public static CustomToast makeText(int resId) {
        return makeText(RunningContext.sAppContext.getResources()
                .getText(resId));
    }

}
