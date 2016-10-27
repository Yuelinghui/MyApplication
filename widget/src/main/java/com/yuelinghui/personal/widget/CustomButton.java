package com.yuelinghui.personal.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.yuelinghui.personal.maframe.util.ListUtil;

import java.util.LinkedHashSet;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class CustomButton extends Button implements Observer {


    private static final long PERFORM_DELAY_TIME = 0;
    private LinkedHashSet<Verifiable> mVerifiers = new LinkedHashSet<Verifiable>();
    /**
     * 外部设置的点击监听器
     */
    private OnClickListener mOuterClickListener = null;
    /**
     * button是否处于休眠状态，用于防止按钮响应多次点击
     */
    private boolean mIsSleep = false;

    /**
     * 自动下一步
     */
    private boolean mAutoPerformClick = false;

    private CountDownTimer mClickTimer = new CountDownTimer(500, 500) {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mIsSleep = false;
        }
    };

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 防止timer异常，不能使mIsSleep恢复初始值
        mIsSleep = false;
    };

    public CustomButton(Context context) {
        this(context,null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        super.setOnClickListener(mInternalClick);
    }

    /**
     * 内部点击监听器
     */
    private OnClickListener mInternalClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // 非冻结状态下，才响应点击事件
            if (!mIsSleep) {
                if (isVerify() && mOuterClickListener != null) {
                    mOuterClickListener.onClick(v);
                }
            }
            // 每一次点击都冻结按钮，直至停下咸猪手，500毫秒后，才响应点击事件
            mIsSleep = true;
            mClickTimer.cancel();
            mClickTimer.start();
        }
    };

    @Override
    public void setOnClickListener(OnClickListener outClickListener) {
        mOuterClickListener = outClickListener;
    }

    /**
     * 添加观察者
     *
     * @author wyqiuchunlong
     * @param verifier
     */
    public void observer(Verifiable verifier) {
        if (this.isEnabled()) {
            this.setEnabled(false);
        }

        // 校验：如果当前verifier属于view 但是 不可显示则不做监听
        if ((verifier instanceof View)
                && ((View) verifier).getVisibility() != View.VISIBLE) {
            update(null, null);
            return;
        }
        if (verifier != null && !mVerifiers.contains(verifier)) {
            mVerifiers.add(verifier);
            verifier.addObserver(this);
        }

        update(null, null);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    /**
     * 解除观察
     *
     * @author wyqiuchunlong
     * @param verifier
     */
    public void removeObserver(Verifiable verifier) {

        if (verifier != null) {
            mVerifiers.remove(verifier);
            if (ListUtil.isEmpty(mVerifiers)) {
                mAutoPerformClick = false;
            }
            this.update(null, null);
        }
    }

    /**
     * 清空观察者
     */
    public void clearObserver() {
        if (!ListUtil.isEmpty(mVerifiers)) {
            mVerifiers.clear();
            mAutoPerformClick = false;
            this.update(null, null);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (mAutoPerformClick) {
            if (!ListUtil.isEmpty(mVerifiers)) {
                if (isVerify()) {
                    this.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            performClick();
                        }
                    }, PERFORM_DELAY_TIME);
                }
            }
        } else {
            for (Verifiable verifier : mVerifiers) {
                if (verifier.isBlank()) {
                    CustomButton.this.setEnabled(false);
                    return;
                }
            }
            CustomButton.this.setEnabled(true);
        }
    }

    /**
     * 是否已通过验证
     *
     * @return
     */
    private boolean isVerify() {
        for (Verifiable verifier : mVerifiers) {
            if (!verifier.verify()) {
                return false;
            }
        }
        return true;
    }

    public int getVerifiersSize() {
        if (ListUtil.isEmpty(mVerifiers)) {
            return 0;
        }
        return mVerifiers.size();
    }
}
