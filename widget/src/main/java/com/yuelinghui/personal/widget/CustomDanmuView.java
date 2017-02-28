package com.yuelinghui.personal.widget;

import android.animation.Animator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;
import com.yuelinghui.personal.maframe.util.LocalDisplay;
import com.yuelinghui.personal.widget.image.CustomImageView;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuelinghui on 17/2/28.
 */

public class CustomDanmuView extends RelativeLayout implements View.OnClickListener {

    private List<View> mChildList;
    private boolean mIsWorking = false;
    private Context mContext;
    private final int mMaxShowNum = 30;
    private final int mRowNum = 6;

    private int mDelayDuration = 1000;

    private int textViewHeight = LocalDisplay.dp2px(150 / (mRowNum - 1));
    private int textViewInnerMargin = LocalDisplay.dp2px(150 / (mRowNum + 2));
    private int textViewFirstMarginTop = LocalDisplay.dp2px(10);

    private List<DanmuInfo> mOptions;
    private boolean isFirst = true;

    public static enum XCDirection {
        FROM_RIGHT_TO_LEFT,
        FORM_LEFT_TO_RIGHT
    }

    public enum XCAction {
        SHOW, HIDE
    }

    private XCDirection mDirection = XCDirection.FROM_RIGHT_TO_LEFT;

    private Handler mHandler;

    public CustomDanmuView(Context context) {
        this(context, null, 0);
    }

    public CustomDanmuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomDanmuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mChildList = new ArrayList<>();
        mHandler = new AnimationHandler(this);
    }

    public boolean isWorking() {
        return mIsWorking;
    }

    public void setDirection(XCDirection direction) {
        mDirection = direction;
    }

    public void initDanmuItemViews(List<DanmuInfo> options) {
        stop();
        mChildList.clear();
        mOptions = options;
        for (int i = 0; i < mMaxShowNum && i < options.size(); i++) {
            createDanmuView(i, mOptions.get(i), false);
        }
    }

    public void createDanmuView(int index, DanmuInfo option, boolean reset) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_danmu_item, null);
        TextView textView = (TextView) view.findViewById(R.id.barrage_text);
        textView.setText(option.getContent());

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, textViewHeight);
        int row = index % mRowNum;

        lp.topMargin = row * (textViewInnerMargin + textViewHeight) + textViewFirstMarginTop;

        view.setLayoutParams(lp);
        view.setId(index);
        view.setOnClickListener(this);
        this.addView(view);

        if (reset) {
            mChildList.set(index, view);
        } else {
            mChildList.add(index, view);
        }
    }

    @Override
    public void onClick(View view) {
        // 弹幕点击
        int id = view.getId();
        if (id >= 0 && id < mOptions.size()) {
            TextView textView = (TextView) view.findViewById(R.id.barrage_text);
            view.setBackgroundResource(R.drawable.bg_danmu_selected);
            textView.setTextColor(mContext.getResources().getColor(R.color.black));
        }
    }

    public void start() {
        this.setVisibility(View.VISIBLE);
        if (isFirst) {
            for (int i = 0; i < mRowNum && i < mChildList.size(); i++) {
                mHandler.sendEmptyMessageDelayed(i, i * mDelayDuration);
            }
            isFirst = false;
        }

        mIsWorking = true;
    }

    public void hide() {
        this.setVisibility(View.GONE);
        mIsWorking = false;
    }

    public void stop() {
        this.setVisibility(View.GONE);
        for (int i = 0; i < mChildList.size(); i++) {
            mChildList.get(i).clearAnimation();
            mHandler.removeMessages(i);
        }
        mIsWorking = false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            if (lp.leftMargin <= 0) {
                if (mDirection == XCDirection.FORM_LEFT_TO_RIGHT) {
                    view.layout(-view.getMeasuredWidth()
                            , lp.topMargin
                            , 0
                            , lp.topMargin + view.getMeasuredHeight());
                } else {
                    view.layout(RunningEnvironment.sScreenWidth
                            , lp.topMargin
                            , RunningEnvironment.sScreenWidth + view.getMeasuredWidth()
                            , lp.topMargin + view.getMeasuredHeight());
                }

            }
        }
    }


    private void switchAnimation(final XCAction action) {
        AlphaAnimation animation;
        if (action == XCAction.HIDE) {
            animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(400);
        } else {
            animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(1000);
        }
        CustomDanmuView.this.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (action == XCAction.HIDE) {
                    CustomDanmuView.this.setVisibility(View.GONE);
                } else {
                    CustomDanmuView.this.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    static class AnimationHandler extends Handler {

        private WeakReference<CustomDanmuView> danmuViewWeakReference;

        public AnimationHandler(CustomDanmuView view) {
            danmuViewWeakReference = new WeakReference<CustomDanmuView>(view);
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            CustomDanmuView view = danmuViewWeakReference.get();
            if (view == null || !view.isWorking())
                return;

            final int pos = msg.what;

            if (pos >= view.mChildList.size())
                return;

            ViewPropertyAnimator animator;

            int parentWidth = view.getWidth();
            int viewWidth = view.mChildList.get(pos).getWidth();

            if (view.mDirection == XCDirection.FROM_RIGHT_TO_LEFT) {
                animator = view.mChildList.get(msg.what).animate()
                        .translationXBy(-(parentWidth + viewWidth));
            } else {
                animator = view.mChildList.get(msg.what).animate()
                        .translationXBy(parentWidth + viewWidth);
            }

            View barrageView = view.mChildList.get(msg.what);
            if (barrageView.getTag() == null && view.mOptions.get(msg.what) != null) {
                barrageView.setTag(view.mOptions.get(msg.what).getId());
                CustomImageView avatar = (CustomImageView) barrageView.findViewById(R.id.barrage_image);
                avatar.setImageUrl(view.mOptions.get(msg.what).getImage());
            }
            animator.setDuration(7000);
            animator.setInterpolator(new LinearInterpolator());
            animator.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    CustomDanmuView view = danmuViewWeakReference.get();
                    if (view == null)
                        return;
                    View curView = view.mChildList.get(pos);
                    view.removeView(curView);
                    int index = (pos + view.mRowNum) % (Math.min(view.mOptions.size(), view.mChildList.size()));
                    view.createDanmuView(index, view.mOptions.get(index), true);
                    view.mHandler.sendEmptyMessageDelayed(index, view.mDelayDuration);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();
        }
    }

    public static class DanmuInfo implements Serializable {
        private int id = 0;
        private String content = "";
        private String image = "";

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
