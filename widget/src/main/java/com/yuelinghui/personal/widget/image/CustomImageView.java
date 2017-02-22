package com.yuelinghui.personal.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;
import com.yuelinghui.personal.maframe.result.ResultHandler;
import com.yuelinghui.personal.widget.R;
import com.yuelinghui.personal.widget.core.ImageManager;
import com.yuelinghui.personal.widget.core.LocalImageCache;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class CustomImageView extends ImageView {


    private static final int STOP_ANIMATION = 0;

    /**
     * 图片默认动画（渐变400ms）
     */
    private static Animation sFadeAnim = null;
    /**
     * 是否缩放
     */
    private boolean mShowFullScreen = false;
    /**
     * 是否显示为缩略图
     */
    private boolean mShowThumb = false;

    /**
     * 是否有渐变动画
     */
    private boolean hasFadeAnim = false;

    /**
     * 动画
     */
    private Animation mAnim = null;
    /**
     * 最小执行时间
     */
    private int mDuration = 0;
    /**
     * 图片下载器
     */
    private ImageManager mImageManager = null;
    /**
     * 服务端url
     */
    private String mUrl = null;

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public CustomImageView(Context context) {
        super(context);
        initView(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * 初始化
     *
     */
    private void initView(Context context) {
        mImageManager = new ImageManager(context);
        if (sFadeAnim == null) {
            sFadeAnim = AnimationUtils.loadAnimation(getContext(),
                    R.anim.fade_in);
        }
    }

    /**
     * 初始设置渐变
     *
     * @param fadeEnable
     */
    public void setFadeEnable(boolean fadeEnable) {
        this.hasFadeAnim = fadeEnable;
    }

    /**
     * 设置图片路径
     *
     * @param url
     */
    public void setImageUrl(String url) {
        setImageUrl(url, null, null);
    }

    /**
     * 设置图片路径
     *
     * @param url
     */
    public void setImageUrl(String url, Bitmap bitmap) {
        setImageUrl(url, bitmap, null);
    }

    /**
     * 设置图片路径
     *
     * @param url
     */
    public void setImageUrl(String url, int resourceId) {
        setImageUrl(url, resourceId, null);
    }

    /**
     * 设置ImageView 图片url
     *
     * @param url
     * @param resourceId
     * @param cacheFirst
     *            是否先检查缓存
     */
    public void setImageUrl(String url, final int resourceId, boolean cacheFirst) {
        if (cacheFirst) {
            Bitmap cacheBitmap = mImageManager.loadImageFromCache(url);
            if (cacheBitmap != null) {
                updateBitmap(cacheBitmap, null);
                return;
            }
        }
        setImageUrl(url, resourceId, null);
    }

    /**
     * 设置图片路径
     *
     * @param url
     */
    public void setImageUrl(String url, final int resourceId,
                            final Cutter cutter) {
        // 记录当前处理的url用来处理网络返回时验证数据
        setTag(url);

        Bitmap defaultBitmap = null;

        // 缓存读取布局
        if (resourceId != 0) {
            LocalImageCache resourceCache = LocalImageCache.getInstance();
            String key = String.valueOf(resourceId);
            defaultBitmap = resourceCache.getBitmap(key);
            if (defaultBitmap == null) {
                defaultBitmap = BitmapFactory.decodeResource(
                        getContext().getResources(), resourceId);
            }
            resourceCache.saveBitmap(key, defaultBitmap);
        }

        // 图片边框裁减
        if (defaultBitmap != null && cutter != null) {
            defaultBitmap = cutter.cut(defaultBitmap);
        }

        // 设置默认图标
        if (!prepareUrlImage(url, defaultBitmap)) {
            return;
        }
        loadUrlImage(url, cutter, resourceId, defaultBitmap);
    }

    /**
     * 设置图片路径
     *
     * @param url
     */
    public void setImageUrl(final String url, final Bitmap bitmap,
                            final Cutter cutter) {
        // 记录当前处理的url用来处理网络返回时验证数据
        setTag(url);

        Bitmap defaultBitmap = bitmap != null && cutter != null ? cutter
                .cut(bitmap) : null;

        if (!prepareUrlImage(url, defaultBitmap)) {
            return;
        }

        loadUrlImage(url, cutter, 0, defaultBitmap);
    }

    /**
     * 设置图片路径（具有缩放功能）
     *
     * @param showThumb
     * @param showFullScreen
     */
    public void setImageUrl(String url, boolean showThumb, boolean showFullScreen) {
        this.mShowFullScreen = showFullScreen;
        this.mShowThumb = showThumb;

        setImageUrl(url, null, null);
    }

    /**
     * 检查url合法性和Image处理
     *
     * @param url
     * @param defaultBitmap
     * @return
     */
    private boolean prepareUrlImage(String url, Bitmap defaultBitmap) {
        boolean result = true;

        if (TextUtils.isEmpty(mUrl) && TextUtils.isEmpty(url)) {
            mUrl = null;
            result = false;
        } else {
            if (TextUtils.isEmpty(url)) {
                mUrl = null;
                result = false;
            } else if (!TextUtils.isEmpty(mUrl) && mUrl.equals(url)) {
                return false;
            }
        }

        // 设置url对应的初始值
        updateBitmap(defaultBitmap);

        return result;
    }

    /**
     * 装载url对应的图像
     *
     * @param url
     * @param cutter
     */
    private void loadUrlImage(final String url, final Cutter cutter,
                              final int defaultResId, final Bitmap defaultBitmap) {
        mImageManager.loadImage(url, new ResultHandler<Bitmap>() {

            @Override
            protected boolean onStart() {
                return true;
            }

            @Override
            protected void onFinish() {
            }

            @Override
            protected void onSuccess(Bitmap data, String message) {
                if (getTag() != null && getTag().equals(url)) {
                    // 只接受最后一次设置的url
                    if (data != null) {
                        if (mShowFullScreen) {
                            updateBitmap(zoomImg(data));
                        } else if (mShowThumb) {
                            updateBitmap(thumbImg(data));
                        } else {
                            updateBitmap(data, cutter);
                        }

                        mUrl = url;
                    } else if (defaultBitmap != null) {
                        updateBitmap(defaultBitmap, cutter);
                    } else if (defaultResId != 0) {
                        setImageUrl("", defaultResId, cutter);
                    } else {
                        updateBitmap(null, cutter);
                    }
                }
            }

        });
    }

    /**
     * 更新图片
     *
     * @param bitmap
     * @param cutter
     */
    private void updateBitmap(Bitmap bitmap, Cutter cutter) {
        mUrl = null;

        if (bitmap == null) {
            return;
        }

        if (cutter != null) {
            setImageBitmap(cutter.cut(bitmap));
        } else {
            setImageBitmap(bitmap);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        // 当之前没有图标，且可以执行渐变效果，切有渐变动画时，执行渐变动画
        if (getDrawable() == null && hasFadeAnim && sFadeAnim != null) {
            this.startAnimation(sFadeAnim);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            if (hasFadeAnim && sFadeAnim != null) {
                this.startAnimation(sFadeAnim);
            }
        }
    }

    /**
     * 更新图片 setImageResource会导致低效率，慎用
     *
     * @param bitmap
     */
    private void updateBitmap(Bitmap bitmap) {
        mUrl = null;
        if (bitmap != null) {
            setImageBitmap(bitmap);
        } else {
            setImageBitmap(null);
        }
    }

    /**
     * 设置动画最小执行时间
     *
     * @author wyqiuchunlong
     * @param anim
     * @param duration
     */
    public void setDurationCloseAnimation(Animation anim, int duration) {
        mAnim = anim;
        mDuration = duration;
    }

    /**
     * 开始动画
     *
     * @author wyqiuchunlong
     */
    public void startDurationCloseAnimation() {
        if (mAnim != null) {
            super.startAnimation(mAnim);
        }
    }

    /**
     * 结束动画
     *
     * @author wyqiuchunlong
     */
    public void stopDurationCloseAnimation() {

        if (mAnim != null) {
            mHandler.sendEmptyMessageDelayed(STOP_ANIMATION, mDuration);
        }
    }

    /**
     * 延迟关闭动画 handler
     */
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg != null) {
                switch (msg.what) {
                    case STOP_ANIMATION:
                        if (mAnim != null) {
                            mAnim.reset();
                            clearAnimation();
                        }
                        break;
                }
            }
        }

    };

    /**
     * 设置循环动画
     *
     * @author wyqiuchunlong
     * @param animId
     */
    public void setAnim(int animId, boolean oneShot) {
        // 设置动画背景
        setImageResource(animId);
        AnimationDrawable anmi = (AnimationDrawable) getDrawable();
        anmi.setOneShot(oneShot);
        if (anmi.isRunning()) {
            anmi.stop();
        }
        anmi.start();
    }

    /**
     * 设置图标是否可用，不可用时显示30%透明
     *
     * @param enable
     */
    @SuppressWarnings("deprecation")
    public void setEnable(boolean enable) {
        if (enable) {
            setAlpha(255);
        } else {
            setAlpha(77);
        }
    }

    /**
     * 取得url
     *
     * @return
     */
    public String getLoadUrl() {
        return mUrl;
    }

    /**
     * 缩放图片
     *
     * @param loadImg
     * @return
     */
    private Bitmap zoomImg(Bitmap loadImg) {

        if (null == loadImg) {
            return null;
        }

        int width = loadImg.getWidth();
        int height = loadImg.getHeight();
        float scaleWidth = ((float) RunningEnvironment.sScreenWidth) / width;
        float scaleHeight = ((float) RunningEnvironment.sScreenHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap zoomBmp = Bitmap.createBitmap(loadImg, 0, 0, loadImg.getWidth(),
                loadImg.getHeight(), matrix, true);
        return zoomBmp;
    }

    private Bitmap thumbImg(Bitmap loadImg) {

        if (null == loadImg) {
            return null;
        }

        float width = RunningEnvironment.sScreenWidth * 0.25f;
        float height = RunningEnvironment.sScreenHeight * 0.25f;
        float bitWidth = loadImg.getWidth();
        float bitHeight = loadImg.getHeight();

        float scale = Math.max(bitWidth / width, bitHeight / height);
        if (scale > 1) {
            bitWidth /= scale;
            bitHeight /= scale;
        }

        return Bitmap.createScaledBitmap(loadImg, (int) (bitWidth),
                (int) (bitHeight), true);
    }

    /**
     * 保存
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);
        ss.hasFadeAnim = hasFadeAnim ? 1 : 0;
        return ss;
    }

    /**
     * 恢复
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        hasFadeAnim = ss.hasFadeAnim == 1 ? true : false;
    }

    /**
     * User interface state that is stored by TextView for implementing
     * {@link View#onSaveInstanceState}.
     */
    public static class SavedState extends BaseSavedState {
        private int hasFadeAnim = 0;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(hasFadeAnim);
        }

        private SavedState(Parcel in) {
            super(in);
            hasFadeAnim = in.readInt();
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
