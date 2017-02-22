package com.yuelinghui.personal.widget.toast;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;
import com.yuelinghui.personal.widget.R;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class CustomExitToast extends Toast {


    /**
     * 当前展示的toast
     */
    private static CustomExitToast sShowingToast = null;
    /**
     * 提示长短时长的字符限制
     */
    static private final int CHAR_DURATION_LIMIT = 20;

    private CharSequence mToastStr = null;

    private CustomExitToast(Context context, CharSequence text) {
        super(context);
        mToastStr = text;
    }

    @Override
    public void show() {
        // 如果已经退出应用，则不弹出toast
//        if (RunningEnvironment.sAppData.sIsExitApp) {
//            return;
//        }

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
    }

    @Override
    public void cancel() {
        // 当前toast关闭时设置当前展示的toast为空
        sShowingToast = null;
        super.cancel();
    }

    public static CustomExitToast makeText(CharSequence text) {
        CustomExitToast toast = new CustomExitToast(RunningEnvironment.sAppContext, text);
        LayoutInflater inflate = (LayoutInflater) RunningEnvironment.sAppContext
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
    public static CustomExitToast makeText(int resId) {
        return makeText(RunningEnvironment.sAppContext.getResources()
                .getText(resId));
    }
}
