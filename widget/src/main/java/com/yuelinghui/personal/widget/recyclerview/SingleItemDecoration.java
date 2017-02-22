package com.yuelinghui.personal.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.yuelinghui.personal.maframe.util.LocalDisplay;
import com.yuelinghui.personal.widget.R;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class SingleItemDecoration extends RecyclerView.ItemDecoration {

    private final int recyclerViewPaddingBothSide; //左右间距,由recycler传入
    private final int recyclerViewPaddingMarginTop; //第一个item顶部间距,由recycler传入
    private final int recyclerViewPaddingMargin; //上下两个item内间距,由recycler传入
    private final Drawable recyclerViewDecoration; //分割线颜色
    private final int mOrientation; //垂直还是水平
    private int mFootViewCount;

    public SingleItemDecoration(Context context, AttributeSet attrs, int footViewCount) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerView);
        recyclerViewPaddingBothSide = a.getDimensionPixelSize(R.styleable.RefreshView_item_both_side, LocalDisplay.dp2px(5));
        recyclerViewPaddingMarginTop = a.getDimensionPixelSize(R.styleable.RefreshView_item_margin_top, 0);
        recyclerViewPaddingMargin = a.getDimensionPixelSize(R.styleable.RefreshView_item_margin, LocalDisplay.dp2px(5));
        recyclerViewDecoration = new ColorDrawable(a.getColor(R.styleable.RefreshView_decoration_color, ContextCompat.getColor(context
                , R.color.transparent)));
        mOrientation = a.getInteger(R.styleable.RefreshView_orientation, OrientationHelper.VERTICAL);
        this.mFootViewCount = footViewCount;
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == OrientationHelper.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager != null && !(layoutManager instanceof LinearLayoutManager)) {
            throw new RuntimeException("FeedItemDecoration must be used in LinearLayoutManager!");
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int left = recyclerViewPaddingBothSide;
        int right = parent.getWidth() - recyclerViewPaddingBothSide;
        final int childCount = parent.getChildCount();
        //childCount包括footerView,对footerView的底边距另作处理
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = mFootViewCount > 0 && i == childCount - 1 ? top + recyclerViewPaddingMargin : top;
            recyclerViewDecoration.setBounds(left, top, right, bottom);
            recyclerViewDecoration.draw(c);
            //左边距
            recyclerViewDecoration.setBounds(0, child.getTop(), recyclerViewPaddingBothSide, child.getBottom() + recyclerViewPaddingMargin);
            recyclerViewDecoration.draw(c);
//                右边距
            recyclerViewDecoration.setBounds(right, child.getTop(), parent.getWidth(), child.getBottom() + recyclerViewPaddingMargin);
            recyclerViewDecoration.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int top = recyclerViewPaddingBothSide;
        int bottom = parent.getHeight() - recyclerViewPaddingBothSide;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + recyclerViewDecoration.getIntrinsicHeight();
            recyclerViewDecoration.setBounds(left, top, right, bottom);
            recyclerViewDecoration.draw(c);
            //上边距
            recyclerViewDecoration.setBounds(child.getLeft(), 0, child.getRight() + recyclerViewPaddingMargin, recyclerViewPaddingBothSide);
            recyclerViewDecoration.draw(c);
//                下边距
            recyclerViewDecoration.setBounds(child.getLeft(), child.getBottom(), child.getRight() + recyclerViewPaddingMargin, child.getBottom() + recyclerViewPaddingMargin);
            recyclerViewDecoration.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
        int itemMarginTop = lp.getViewLayoutPosition() == 0 ? recyclerViewPaddingMarginTop : recyclerViewPaddingMargin;

        if (mOrientation == OrientationHelper.VERTICAL) {
            outRect.set(recyclerViewPaddingBothSide, itemMarginTop, recyclerViewPaddingBothSide, 0);
        } else {
            outRect.set(itemMarginTop, recyclerViewPaddingBothSide, 0, recyclerViewPaddingBothSide);
        }
    }
}
