package com.yuelinghui.personal.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuelinghui.personal.widget.CustomButton;
import com.yuelinghui.personal.widget.R;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class CustomDialog extends CustomAttachableDiable {


    /**
     * contxt
     */
    private Context mContext;
    /**
     * 左侧关闭按钮
     */
    private CustomButton mCancelBtn;
    /**
     * 按钮文案
     */
    private String mCancelStr;
    /**
     * 右侧确定按钮
     */
    private CustomButton mOkBtn;
    /**
     * 按钮文案
     */
    private String mOkStr;
    /**
     * 右侧确定按钮点击监听
     */
    private View.OnClickListener mOkClick;
    /**
     * 左侧取消按钮点击监听
     */
    private View.OnClickListener mCancelClick;
    /**
     * title 区域容器
     */
    private View mTitleLayout = null;
    /**
     * 中间内容区域容器
     */
    private LinearLayout mContentLayout = null;
    /**
     * 内容区域定制的view
     */
    private View mCustomView = null;
    /**
     * 不设置按钮时，是否可以按返回键关闭
     */
    private boolean isCancelable = true;
    /**
     * 标题textview
     */
    private TextView mTitleTxt = null;
    /**
     * 标题
     */
    private String mTitle;
    /**
     * 提示textview
     */
    private TextView mMsgTxt;
    /**
     * 提示
     */
    private String mMsg;
    /**
     * 确认按钮是否展示
     */
    private boolean mOkBtnVisibal = false;
    /**
     * 关闭按钮是否展示
     */
    private boolean mCancelBtnVisibal = false;

    /**
     *
     * @param context
     */
    public CustomDialog(Context context) {
        super(context, R.style.cp_dialog);
        this.mContext = context;
        // 设定按钮的初始化文本
        mOkStr = mContext.getString(R.string.sure);
        mCancelStr = mContext.getString(R.string.cancel);
        // getWindow().setBackgroundDrawableResource(R.drawable.icon_transparent);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        setCancelable(false);

        // 标题布局
        mTitleLayout = findViewById(R.id.title);
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleLayout.setVisibility(View.VISIBLE);
        } else {
            mTitleLayout.setVisibility(View.GONE);
        }

        // 标题view
        mTitleTxt = (TextView) findViewById(R.id.title_text);
        mTitleTxt.setText(mTitle);

        // 内容view
        mContentLayout = (LinearLayout) findViewById(R.id.layout_view);

        // 信息文案
        mMsgTxt = (TextView) findViewById(R.id.txt_msg);
        mMsgTxt.setText(mMsg);

        // 关闭按钮
        mCancelBtn = (CustomButton) findViewById(R.id.btn_cancel);
        mCancelBtn.setOnClickListener(mDefaultCancelClick);

        // 成功按钮
        mOkBtn = (CustomButton) findViewById(R.id.btn_ok);
        mOkBtn.setOnClickListener(mDefaultOkClick);

        // 重新布局
        setLayout();

        WindowManager m = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();

        p.width = (int) (d.getWidth() * 0.8);
        getWindow().setAttributes(p);
        getWindow().setGravity(Gravity.CENTER);

    }

    /**
     * 设置标题
     *
     * @param title
     *            标题
     * @return
     */
    public CustomDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * 设置自定义view
     *
     * @param view
     * @return
     */
    public CustomDialog setView(View view) {
        this.mCustomView = view;
        return this;
    }

    /**
     * 设置具体信息
     *
     * @param msg
     *            具体信息
     * @return
     */
    public CustomDialog setMsg(String msg) {
        this.mMsg = msg;
        return this;
    }

    /**
     * 设定确定按钮，设定后确定按钮可见
     *
     * @param okClickListener
     * @return
     */
    public CustomDialog setOkButton(String text,
                                View.OnClickListener okClickListener) {
        this.mOkBtnVisibal = true;
        this.mOkClick = okClickListener;
        if (!TextUtils.isEmpty(text)) {
            mOkStr = text;
        }
        return this;
    }

    /**
     * 设定取消按钮，设定后取消按钮可见
     *
     * @param cancelClickListener
     * @return
     */
    public CustomDialog setCancelButton(String text,
                                    View.OnClickListener cancelClickListener) {
        this.mCancelBtnVisibal = true;
        this.mCancelClick = cancelClickListener;
        if (!TextUtils.isEmpty(text)) {
            mCancelStr = text;
        }
        return this;
    }

    @Override
    public void show() {
        if (!this.isShowing()) {
            super.show();
        }
    }

    /**
     * 2秒后无按钮对话框自动消失
     */
    private CountDownTimer mTimer = new CountDownTimer(2000, 2000) {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (CustomDialog.this.isShowing()) {
                CustomDialog.this.dismiss();
            }
        }
    };

    /**
     * 重新布局
     */
    private void setLayout() {

        // 设置自定义内容
        if (mCustomView != null) {
            mContentLayout.removeAllViews();
            mContentLayout.addView(mCustomView);
        }

        View splider = findViewById(R.id.view_splider);
        // 设置关闭按钮是否显示
        mCancelBtn.setText(mCancelStr);
        if (mCancelBtnVisibal) {
            mCancelBtn.setVisibility(View.VISIBLE);
            mCancelBtn
                    .setBackgroundResource(R.drawable.custom_dialog_left_btn_bg);
            splider.setVisibility(mOkBtnVisibal ? View.VISIBLE : View.GONE);
        } else {
            mCancelBtn.setVisibility(View.GONE);
        }

        // 设置确认按钮是否显示
        mOkBtn.setText(mOkStr);
        if (mOkBtnVisibal) {
            mOkBtn.setVisibility(View.VISIBLE);
            mOkBtn.setBackgroundResource(R.drawable.custom_dialog_left_btn_bg);
            splider.setVisibility(mCancelBtnVisibal ? View.VISIBLE : View.GONE);
        } else {
            mOkBtn.setVisibility(View.GONE);
        }

        // 如果都不展示。设置2秒后自动关闭
        if (!mCancelBtnVisibal && !mOkBtnVisibal) {
            if (isCancelable) {
                mTimer.start();
                setCancelable(true);
            }
            findViewById(R.id.btn_layout).setVisibility(View.GONE);
        }
    }

    /**
     * 没有按钮时，设置是否可关闭 默认是可关闭的
     *
     * @author wyqiuchunlong
     * @param isCancelable
     */
    public void setNoButtonCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    @Override
    public void cancel() {
        super.cancel();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    /**
     * 默认成功点击监听
     */
    private View.OnClickListener mDefaultOkClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mOkClick != null) {
                mOkClick.onClick(v);
            }
            CustomDialog.super.dismiss();
        }
    };

    /**
     * 默认关闭点击监听
     */
    private View.OnClickListener mDefaultCancelClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mCancelClick != null) {
                mCancelClick.onClick(v);
            }
            CustomDialog.super.dismiss();
        }
    };


}
