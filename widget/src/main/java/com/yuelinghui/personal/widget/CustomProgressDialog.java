package com.yuelinghui.personal.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class CustomProgressDialog extends Dialog {

    private ImageView mImageView ;
    private TextView mMsgTxt;

    private Animation operatingAnim;

    public CustomProgressDialog(Context context) {
        super(context);
        initDialog(context);
    }

    public CustomProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        initDialog(context);
    }

    protected CustomProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initDialog(context);
    }

    private void initDialog(Context context) {
        setContentView(R.layout.progress_dialog_view);
        getWindow().getAttributes().gravity = Gravity.CENTER;

        mImageView = (ImageView) findViewById(R.id.img_loading);
        mMsgTxt = (TextView) findViewById(R.id.txt_loading_msg);

        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }

    @Override
    public void show() {
        super.show();
        mImageView.clearAnimation();
        mImageView.startAnimation(operatingAnim);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mImageView.clearAnimation();
    }

    public CustomProgressDialog setMessage(String strMessage) {
        if (mMsgTxt != null) {
            mMsgTxt.setText(strMessage);
        }
        return this;
    }
}
