package com.yuelinghui.personal.widget.quicksidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.yuelinghui.personal.widget.R;

/**
 * Created by yuelinghui on 16/12/5.
 * 快速查找侧边栏
 */

public class QuickSideBarTipsItemView extends View {
    private int mCornerRadius;
    private Path mBackgroundPath;
    private RectF mBackgroundRect;
    private Paint mBackgroundPaint;

    private String mText = "";
    private Paint mTextPaint;

    private int mWidth;
    private int mItenHeight;
    private float mTextSize;
    private int mTextColor;
    private int mBackgroundColor;
    private int mCenterTextStartX;
    private int mCenterTextStartY;

    private float[] mRadii;

    public QuickSideBarTipsItemView(Context context) {
        this(context, null);
    }

    public QuickSideBarTipsItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickSideBarTipsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mBackgroundPath = new Path();
        mBackgroundRect = new RectF();
        mTextColor = context.getResources().getColor(R.color.black);
        mBackgroundColor = context.getResources().getColor(R.color.gray);
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.size_large);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QuickSideBarView);
            mTextColor = a.getColor(R.styleable.QuickSideBarView_sidebarTextColor, mTextColor);
            mBackgroundColor = a.getColor(R.styleable.QuickSideBarView_sidebarBackgroundColor, mBackgroundColor);
            mTextSize = a.getDimension(R.styleable.QuickSideBarView_sidebarTextSize, mTextSize);
            a.recycle();
        }
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mRadii = new float[]{mCornerRadius, mCornerRadius,
                mCornerRadius, mCornerRadius, 0, 0, mCornerRadius, mCornerRadius};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth();
        mItenHeight = getWidth();
        mCornerRadius = (int) (mWidth * 0.5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mText)) {
            return;
        }
        canvas.drawColor(getContext().getResources().getColor(R.color.transparent));

        mBackgroundRect.set(0, 0, mWidth, mItenHeight);
        mBackgroundPath.addRoundRect(mBackgroundRect, mRadii, Path.Direction.CW);

        canvas.drawPath(mBackgroundPath, mBackgroundPaint);
        canvas.drawText(mText, mCenterTextStartX, mCenterTextStartY, mTextPaint);
    }

    /**
     * 设置文字
     * @param text
     */
    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mText = text;
        Rect rect = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), rect);
        mCenterTextStartX = (int) ((mWidth - rect.width()) * 0.5);
        mCenterTextStartY = mItenHeight - rect.height();
        invalidate();
    }
}
