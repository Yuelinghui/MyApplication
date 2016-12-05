package com.yuelinghui.personal.widget.quicksidebar;

/**
 * Created by yuelinghui on 16/12/5.
 */

public interface OnQuickSideBarTouchListener {
    void onLetterChanged(String letter, int position, float y);

    void onLetterTouching(boolean touching);
}
