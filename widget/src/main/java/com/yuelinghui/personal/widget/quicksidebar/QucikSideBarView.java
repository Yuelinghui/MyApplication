package com.yuelinghui.personal.widget.quicksidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yuelinghui.personal.maframe.util.ListUtil;
import com.yuelinghui.personal.widget.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yuelinghui on 16/12/5.
 */

public class QucikSideBarView extends View {
    private List<String> mLetters;
    private int mChoose = -1;
    private Paint mPaint;
    private float mTextSize;
    private float mTextSizeChoose;
    private int mTextColor;
    private int mTextColorChoose;
    private int mWidth;
    private int mHeight;
    private float mItemHeight;
    private float mItemStartY;

    private OnQuickSideBarTouchListener mListener;

    public QucikSideBarView(Context context) {
        this(context, null);
    }

    public QucikSideBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QucikSideBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mLetters = Arrays.asList(context.getResources().getStringArray(R.array.quickSideBarLetters));
        mPaint = new Paint();
        mTextColor = context.getResources().getColor(R.color.white);
        mTextColorChoose = context.getResources().getColor(R.color.black);
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.size_small);
        mTextColorChoose = context.getResources().getDimensionPixelOffset(R.dimen.size_large);
        mItemHeight = context.getResources().getDimensionPixelSize(R.dimen.height_small);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.QuickSideBarView);

            mTextColor = a.getColor(R.styleable.QuickSideBarView_sidebarTextColor, mTextColor);
            mTextColorChoose = a.getColor(R.styleable.QuickSideBarView_sidebarTextColorChoose, context.getResources().getColor(R.color.black));
            mTextSize = a.getDimension(R.styleable.QuickSideBarView_sidebarTextSize, mTextSize);
            mTextSizeChoose = a.getDimension(R.styleable.QuickSideBarView_sidebarTextSizeChoose, mTextSizeChoose);
            mItemHeight = a.getDimension(R.styleable.QuickSideBarView_sidebarItemHeight, mItemHeight);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mItemStartY = (mHeight - mLetters.size() * mItemHeight) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (ListUtil.isEmpty(mLetters)) {
            return;
        }
        for (int i = 0; i < mLetters.size(); i++) {
            mPaint.setColor(mTextColor);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            if (i == mChoose) {
                mPaint.setColor(mTextColorChoose);
                mPaint.setFakeBoldText(true);
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                mPaint.setTextSize(mTextSizeChoose);
            }

            Rect rect = new Rect();
            mPaint.getTextBounds(mLetters.get(i), 0, mLetters.get(i).length(), rect);
            float xPos = (float) ((mWidth - rect.width()) * 0.5);
            float yPos = (float) (mItemHeight * i + ((mItemHeight - rect.height()) * 0.5) + mItemStartY);

            canvas.drawText(mLetters.get(i), xPos, yPos, mPaint);
            mPaint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = mChoose;
        final int newChoose = (int) ((y - mItemStartY) / mItemHeight);
        switch (action) {
            case MotionEvent.ACTION_UP:
                mChoose = -1;
                if (mListener != null) {
                    mListener.onLetterTouching(false);
                }
                invalidate();
                break;
            default:
                if (oldChoose != newChoose) {
                    if (newChoose >= 0 && newChoose < mLetters.size()) {
                        mChoose = newChoose;
                        if (mListener != null) {
                            //计算位置
                            Rect rect = new Rect();
                            mPaint.getTextBounds(mLetters.get(mChoose), 0, mLetters.get(mChoose).length(), rect);
                            float yPos = mItemHeight * mChoose + (int) ((mItemHeight - rect.height()) * 0.5) + mItemStartY;
                            mListener.onLetterChanged(mLetters.get(newChoose), mChoose, yPos);
                        }
                    }
                    invalidate();
                }
                //如果是cancel也要调用onLetterUpListener 通知
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (mListener != null) {
                        mListener.onLetterTouching(false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下调用 onLetterDownListener
                    if (mListener != null) {
                        mListener.onLetterTouching(true);
                    }
                }

                break;
        }
        return true;
    }

    public void setLetters(List<String> letters) {
        if (ListUtil.isEmpty(letters)) {
            return;
        }
        mLetters = letters;
        invalidate();
    }

    public void setOnQuickSideBarTouchListener(
            OnQuickSideBarTouchListener listener) {
        mListener = listener;
    }


}
