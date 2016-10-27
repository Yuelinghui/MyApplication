package com.yuelinghui.personal.widget.bannerview;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class FixSpeedScroller extends Scroller {


    private int mDuration = 5000;

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public FixSpeedScroller(Context context) {
        super(context);
    }

    public FixSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
