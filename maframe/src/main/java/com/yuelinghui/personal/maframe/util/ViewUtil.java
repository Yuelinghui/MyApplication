package com.yuelinghui.personal.maframe.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class ViewUtil {


    /**
     * 点击view
     *
     * @param view
     */
    @SuppressLint("Recycle")
    public static void click(View view) {
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
                view.getLeft() + 5, view.getTop() + 5, 0));
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
                view.getLeft() + 5, view.getTop() + 5, 0));
    }

    /**
     * 设置view到左侧的距离
     */
    @SuppressLint("NewApi")
    public static void setX(View view, float x) {
        if (view == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 11) {
            view.setX(x);
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                    .getLayoutParams();
            params.leftMargin = (int) (x);
            view.setLayoutParams(params);
        }
    }

    /**
     * 设置view到头部的距离
     */
    @SuppressLint("NewApi")
    public static void setY(View view, float y) {
        if (view == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 11) {
            view.setY(y);
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                    .getLayoutParams();
            params.topMargin = (int) (y);
            view.setLayoutParams(params);
        }
    }
}
