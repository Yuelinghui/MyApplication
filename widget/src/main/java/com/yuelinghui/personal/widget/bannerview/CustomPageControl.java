package com.yuelinghui.personal.widget.bannerview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yuelinghui.personal.widget.R;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class CustomPageControl extends LinearLayout {


    /**
     * 未选中时圆点的drawable
     */
    private Drawable mNormalIcon;

    /**
     * 选中时圆点的drawable
     */
    private Drawable mSelectedIcon;

    /**
     * 页数
     */
    private int mPageNumber;

    /**
     * 当前页
     */
    private int mCurrentPage = 0;

    /**
     * 圆点之间的margin
     */
    private int mMargin;

    /**
     * 圆点的大小
     */
    private int mDotWidth;
    /**
     * 圆点的大小
     */
    private int mDotHeight;

    public CustomPageControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(HORIZONTAL);
    }

    /**
     * 设置默认状态的圆点
     *
     * @param dotDrawable
     */
    public void setNormalDot(Drawable dotDrawable) {
        if (dotDrawable != null) {
            mNormalIcon = dotDrawable;
        } else {
            mNormalIcon = getResources().getDrawable(R.drawable.dot_normal);
        }
    }

    /**
     * 设置选中状态的圆点
     *
     * @param dotDrawable
     */
    public void setSelectedDot(Drawable dotDrawable) {
        if (dotDrawable != null) {
            mSelectedIcon = dotDrawable;
        } else {
            mSelectedIcon = getResources().getDrawable(R.drawable.dot_focused);
        }
    }

    /**
     * 设置每个圆点之间的间隔
     *
     * @param margin
     */
    public void setPageDotInterval(int margin) {
        if (margin > 0) {
            mMargin = margin;
        } else {
            mMargin = getResources().getDimensionPixelSize(
                    R.dimen.margin_page_control);
        }
    }

    /**
     * 设置圆点的大小
     *
     * @param size
     */
    public void setPageDotSize(int size) {
        setPageDotSize(size, size);
    }

    /**
     * 设置圆点的大小
     *
     */
    public void setPageDotSize(int width, int height) {
        if (width > 0) {
            mDotWidth = width;
        } else {
            mDotWidth = getResources().getDimensionPixelSize(
                    R.dimen.main_tab_bar_padding);
        }

        if (height > 0) {
            mDotHeight = height;
        } else {
            mDotHeight = getResources().getDimensionPixelSize(
                    R.dimen.main_tab_bar_padding);
        }
    }

    /**
     *
     * 设置页面数量
     *
     * @param pageNumber
     */
    public void setPageNumber(int pageNumber) {
        this.mPageNumber = pageNumber;
        buildPages();
    }

    /**
     * 刷新圆点布局，用于初始化或者由于选中页面更改而刷新选中和未选中的圆点
     */
    public void buildPages() {
        removeAllViews();
        if (mPageNumber > 1) {
            setVisibility(View.VISIBLE);

            for (int i = 0; i < mPageNumber; i++) {
                ImageView dotView = new ImageView(getContext());
                LayoutParams params = null;
                if (i == mCurrentPage) {
                    params = new LayoutParams(mDotWidth, mDotHeight);
                } else {
                    int DotSize = mDotWidth < mDotHeight ? mDotWidth
                            : mDotHeight;
                    params = new LayoutParams(DotSize, DotSize);
                }

                // 第一个圆点不需要leftMargin
                if (i != 0) {
                    params.leftMargin = mMargin;
                }

                dotView.setLayoutParams(params);

                // 根据当前页设置相应的drawable
                if (i == mCurrentPage) {
                    dotView.setImageDrawable(mSelectedIcon);
                } else {
                    dotView.setImageDrawable(mNormalIcon);
                }
                addView(dotView);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    /**
     *
     * 设置当前选中页面
     *
     * @param page
     */
    public void setPage(int page) {
        mCurrentPage = page % mPageNumber;
        buildPages();
    }

    public int getPage() {
        return mCurrentPage;
    }


}
