package com.yuelinghui.personal.widget.quicksidebar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by yuelinghui on 16/12/5.
 */

public class QuickSideBarTipsView extends RelativeLayout {
    private QuickSideBarTipsItemView mTipsView;
    public QuickSideBarTipsView(Context context) {
        this(context,null);
    }

    public QuickSideBarTipsView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QuickSideBarTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    private void initView(Context context,AttributeSet attrs) {
        mTipsView = new QuickSideBarTipsItemView(context,attrs);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mTipsView,params);
    }

    public void setText(String text,float y) {
        mTipsView.setText(text);
        LayoutParams params = (LayoutParams) mTipsView.getLayoutParams();
        params.topMargin = (int) (y - getWidth()/2.8);
        mTipsView.setLayoutParams(params);
    }
}
