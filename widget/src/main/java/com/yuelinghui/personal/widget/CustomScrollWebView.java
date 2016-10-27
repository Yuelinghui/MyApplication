package com.yuelinghui.personal.widget;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class CustomScrollWebView extends WebView {

    /**
     * webview 是否滚动
     */
    private boolean mIsScrolling = true;
    /**
     * 触摸时当前的点
     */
    private PointF mCurPoint = new PointF();

    public CustomScrollWebView(Context context) {
        this(context, null);
    }

    public CustomScrollWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        WebSettings settings = getSettings();
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setSupportZoom(true);
//        settings.setBuiltInZoomControls(true);
//        settings.setDisplayZoomControls(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 当拦截触摸事件到达此位置的时候，返回true，
        // 说明将onTouch拦截在此控件，进而执行此控件的onTouchEvent
        // 和android的触屏事件由上至下一层一层传播有关
        return mIsScrolling;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // webview被点击到，即可滑动
                mIsScrolling = true;
                mCurPoint.x = event.getX();
                mCurPoint.y = event.getY();
                // 通知父控件现在进行的是本控件的操作，不要对我的操作进行干扰
                getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float lastY = event.getY(event.getPointerCount() - 1);
                if (isBottom() && isTop()) {
                    // webview内容展示完全，自身无需滚动
                    mIsScrolling = false;
                } else if (isBottom()) {
                    // 如果到达底部，先设置为不能滚动
                    mIsScrolling = false;
                    // 如果到达底部，但开始向上滚动，那么webview可以滚动
                    if (mCurPoint.y - lastY < 0) {
                        mIsScrolling = true;
                    }
                } else if (isTop()) {
                    // 滑到顶部不能再滑
                    mIsScrolling = false;
                    if ((mCurPoint.y - lastY > 0)) {
                        // 滑动到顶部，向下滑，可以滑到
                        mIsScrolling = true;
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(mIsScrolling);
                break;
            case MotionEvent.ACTION_UP:
                mIsScrolling = false;
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 判断是否到WebView达底部
     */
    @SuppressWarnings("deprecation")
    private boolean isBottom() {
        // WebView的总高度
        float contentHeight = getContentHeight() * getScale();
        // WebView的现高度
        float currentHeight = getHeight() + getScrollY();
        // 之间的差距小于2便认为滑动到底部
        return contentHeight - currentHeight < 1;
    }

    private boolean isTop() {
        // 当ScrollY为0是到达顶部
        return getScrollY() == 0;
    }
}
