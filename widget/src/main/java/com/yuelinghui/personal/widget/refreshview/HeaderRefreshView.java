package com.yuelinghui.personal.widget.refreshview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.yuelinghui.personal.maframe.util.LocalDisplay;
import com.yuelinghui.personal.widget.R;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class HeaderRefreshView extends BaseRefreshView implements Animatable {


    private static final float SCALE_START_PERCENT = 0.5f;
    private static final int ANIMATION_DURATION = 1000;

    private final static float TOWN_RATIO = 0.22f;

    //    private static final float SUN_FINAL_SCALE = 0.75f;
    private static final float SUN_INITIAL_ROTATE_GROWTH = 1.2f;
    private static final float SUN_FINAL_ROTATE_GROWTH = 1.5f;

    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();

    private PullToRefreshView mParent;
    private Matrix mMatrix;
    private Animation mAnimation;

    private int mTop;
    private int mScreenWidth;

    private int mTownHeight;
    private float mTownInitialTopOffset;

    private int mSunSize = LocalDisplay.dp2px(30);
    private float mSunLeftOffset;

    private float mPercent = 0.0f;
    private float mRotate = 0.0f;

    private Bitmap mSun;
    private Bitmap mTown;

    private boolean isRefreshing = false;

    private boolean isNightMode = false;

    public HeaderRefreshView(Context context, final PullToRefreshView parent) {
        this(context, parent, false);
    }

    public HeaderRefreshView(Context context, final PullToRefreshView parent, boolean isNightMode) {
        super(context, parent);

        this.isNightMode = isNightMode;
        mParent = parent;
        mMatrix = new Matrix();

        setupAnimations();
        parent.post(new Runnable() {
            @Override
            public void run() {
                initiateDimens(parent.getWidth());
            }
        });
    }

    public void initiateDimens(int viewWidth) {
        if (viewWidth <= 0 || viewWidth == mScreenWidth) return;

        mScreenWidth = viewWidth;

        mTownHeight = (int) (TOWN_RATIO * mScreenWidth);
        mTownInitialTopOffset = (mParent.getTotalDragDistance() - mTownHeight);

        mSunLeftOffset = 0.3f * (float) mScreenWidth;

        mTop = -mParent.getTotalDragDistance();

        if (isNightMode) {
            createNightBitmaps();
        } else {
            createBitmaps();
        }
    }

    private void createBitmaps() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        mTown = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.header_bg_day, options);
        mTown = Bitmap.createScaledBitmap(mTown, mScreenWidth, (int) (mScreenWidth * TOWN_RATIO), true);
        mSun = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun, options);
        mSun = Bitmap.createScaledBitmap(mSun, mSunSize, mSunSize, true);
    }

    private void createNightBitmaps() {
        mTown = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.header_bg_night);
        mTown = Bitmap.createScaledBitmap(mTown, mScreenWidth, (int) (mScreenWidth * TOWN_RATIO), true);
        mSun = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.moon);
        Matrix matrix = new Matrix();
        int width = mSun.getWidth();
        int height = mSun.getHeight();
        matrix.postRotate(-36f, width / 2, height / 2);
        matrix.postTranslate(width, height);
        mSun = Bitmap.createBitmap(mSun, 0, 0, width,height,matrix, false);
    }

    @Override
    public void setPercent(float percent, boolean invalidate) {
        setPercent(percent);
        if (invalidate) setRotate(percent);
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        mTop += offset;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mScreenWidth <= 0) return;

        final int saveCount = canvas.save();

        canvas.translate(0, mTop);
        canvas.clipRect(0, -mTop, mScreenWidth, mParent.getTotalDragDistance());

        drawSun(canvas);
        drawTown(canvas);

        canvas.restoreToCount(saveCount);
    }

    private void drawTown(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();
        matrix.postTranslate(0, mTownInitialTopOffset);
        canvas.drawBitmap(mTown, matrix, null);
    }

    private void drawSun(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        float dragPercent = mPercent;
        if (dragPercent > 1.0f) { // Slow down if pulling over set height
            dragPercent = (dragPercent + 9.0f) / 10;
        }

        float sunRadius = (float) mSunSize / 2.0f;
        float sunRotateGrowth = SUN_INITIAL_ROTATE_GROWTH;

        float offsetX = mSunLeftOffset;
        float offsetY = (mParent.getTotalDragDistance() / 2) * (1.0f - dragPercent) // Move the sun up
                - mTop; // Depending on Canvas position

        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            sunRotateGrowth += (SUN_FINAL_ROTATE_GROWTH - SUN_INITIAL_ROTATE_GROWTH) * scalePercent;
            matrix.preTranslate(offsetX + (sunRadius - sunRadius), offsetY * 1.0f);
            offsetX += sunRadius;
            offsetY += sunRadius;
        } else {
            matrix.postTranslate(offsetX, offsetY);
            offsetX += sunRadius;
            offsetY += sunRadius;
        }

        float r;
        if (isNightMode) {
            if (isRefreshing) {
                if (mRotate > 0.5) {
                    mRotate = 1 - mRotate;
                }
                r = 130 * mRotate;
                matrix.postRotate(-(r/2), offsetX, offsetY);
            }
        } else {
            r = (isRefreshing ? -360 : 360) * mRotate * (isRefreshing ? 1 : sunRotateGrowth);
            matrix.postRotate(r, offsetX, offsetY);
        }

        canvas.drawBitmap(mSun, matrix, null);
    }

    public void setPercent(float percent) {
        mPercent = percent;
    }

    public void setRotate(float rotate) {
        mRotate = rotate;
        invalidateSelf();
    }

    public void resetOriginals() {
        setPercent(0);
        setRotate(0);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, top);
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void start() {
        mAnimation.reset();
        isRefreshing = true;
        mParent.startAnimation(mAnimation);
    }

    @Override
    public void stop() {
        mParent.clearAnimation();
        isRefreshing = false;
        resetOriginals();
    }

    private void setupAnimations() {
        mAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setRotate(interpolatedTime);
            }
        };
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        mAnimation.setDuration(ANIMATION_DURATION);
    }

}
