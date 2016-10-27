package com.yuelinghui.personal.widget.titlebar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yuelinghui.personal.maframe.util.ListUtil;
import com.yuelinghui.personal.widget.CustomButton;
import com.yuelinghui.personal.widget.R;
import com.yuelinghui.personal.widget.core.RunningContext;
import com.yuelinghui.personal.widget.image.CustomImageView;

import java.util.List;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class CustomTitleBar extends FrameLayout {


    /**
     * 标题
     */
    private TextView mTitleTxt = null;
    /**
     * 标题---右侧按钮
     */
    private CustomButton mTitleRightBtn = null;
    /**
     * 标题---右侧按钮图标
     */
    private CustomImageView mTitleRightImg = null;
    /**
     * 标题---左侧按钮图标
     */
    private CustomImageView mTitleLeftImg = null;
    /**
     * 标题---父布局
     */
    private View mTitleLayout = null;
    /**
     * 自定义标题
     */
    private FrameLayout mTitleCustomLayout = null;
    /**
     *
     */
    private ViewGroup mTilteBaseLayout = null;
    /**
     * 分割线
     */
    private View mTitleDivider = null;
    /**
     * action列表
     */
    private List<CustomAction> mActions = null;

    /**
     * action 菜单
     */
    private CustomActionMenu mActionMenu = null;

    public CustomTitleBar(Context context) {
        this(context, null);
    }

    public CustomTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_title_bar, this,
                true);

        mTitleLayout = findViewById(R.id.layout_title);
        mTitleTxt = (TextView) findViewById(R.id.txt_main_title);
        int maxpixels = 0;
        if (RunningContext.sScreenWidth > 0) {
            maxpixels = RunningContext.sScreenWidth
                    - RunningContext.sAppContext
                    .getResources()
                    .getDimensionPixelOffset(R.dimen.titlebar_width_sub);
        } else {
            maxpixels = RunningContext.sAppContext.getResources()
                    .getDimensionPixelOffset(R.dimen.titlebar_width_avage);
        }
        mTitleTxt.setMaxWidth(maxpixels);
        mTitleRightBtn = (CustomButton) findViewById(R.id.txt_right_title);
        mTitleRightImg = (CustomImageView) findViewById(R.id.img_right_title);
        mTilteBaseLayout = (ViewGroup) findViewById(R.id.layout_base);
        mTitleCustomLayout = (FrameLayout) findViewById(R.id.layout_custom);
        mTitleLeftImg = (CustomImageView) findViewById(R.id.img_back);
        mTitleDivider = findViewById(R.id.view_divider_line);

        mActionMenu = new CustomActionMenu(getContext());

    }

    /**
     * 设置自定义title
     *
     * @param customView
     */
    public void setCustomTitle(View customView) {
        mTitleCustomLayout.removeAllViews();
        mTitleCustomLayout.addView(customView);
        mTilteBaseLayout.setVisibility(View.GONE);
        mTitleCustomLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 设置titlebar背景颜色
     *
     * @param titleBarColor
     */
    public void setTitleBarColor(int titleBarColor) {
        if (titleBarColor == 0) {
            titleBarColor = Color.WHITE;
        }

        mTitleLayout.setBackgroundColor(titleBarColor);
        if (titleBarColor != Color.WHITE) {
            mTitleTxt.setTextColor(Color.WHITE);
            mTitleRightBtn.setTextColor(Color.WHITE);
            setTitleDividerVisiable(false);
        } else {
            int txt_main = RunningContext.sAppContext.getResources().getColor(
                    R.color.txt_main);
            mTitleTxt.setTextColor(txt_main);
            mTitleRightBtn.setTextColor(txt_main);
            setTitleDividerVisiable(true);
        }
    }

    /**
     * 设置复合标题
     *
     * @param title
     *            中间title
     * @param rightTxt
     *            右侧文本，为空则不显示
     * @param rightDarwable
     *            右侧imageview，为空则不显示
     * @param clickable
     *            是否可点击，弹出下拉菜单
     */
    public void setComplexTilte(String title, String rightTxt,
                                Drawable rightDarwable, boolean clickable) {
        if (mTitleTxt == null) {
            return;
        }
        // 设置中间文本、是否可点击
        if (!TextUtils.isEmpty(title)) {
            mTitleTxt.setText(title);
        }

        mTitleTxt.setClickable(clickable);
        if (clickable) {
            mTitleTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.icon_arrow_down, 0);
        } else {
            mTitleTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        // 设置右侧显示文本还是图片
        if (!TextUtils.isEmpty(rightTxt)) {
            mTitleRightBtn.setText(rightTxt);
            mTitleRightBtn.setVisibility(View.VISIBLE);
            mTitleRightImg.setVisibility(View.GONE);
        } else if (rightDarwable != null) {
            mTitleRightBtn.setVisibility(View.GONE);
            mTitleRightImg.setVisibility(View.VISIBLE);
            mTitleRightImg.setImageDrawable(rightDarwable);
        } else {
            mTitleRightBtn.setVisibility(View.GONE);
            mTitleRightImg.setVisibility(View.GONE);
        }

        mTilteBaseLayout.setVisibility(View.VISIBLE);
        mTitleCustomLayout.setVisibility(View.GONE);
    }

    /**
     * 设置标题
     */
    public void setSimpleTitle(String title) {
        if (mTitleTxt == null) {
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            mTitleTxt.setText(title);
        }
        mTitleTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mTitleTxt.setClickable(false);
        mTitleLeftImg.setVisibility(View.GONE);
        mTitleRightBtn.setVisibility(View.GONE);
        mTitleRightImg.setVisibility(View.GONE);

        mTilteBaseLayout.setVisibility(View.VISIBLE);
        mTitleCustomLayout.setVisibility(View.GONE);
    }

    /**
     * 设置标题
     */
    public void setSimpleTitle(String title, int titleTxtColor) {
        if (mTitleTxt == null) {
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            mTitleTxt.setText(title);
        }
        mTitleTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mTitleTxt.setClickable(false);
        mTitleLeftImg.setVisibility(View.GONE);
        mTitleRightBtn.setVisibility(View.GONE);
        mTitleRightImg.setVisibility(View.GONE);

        mTilteBaseLayout.setVisibility(View.VISIBLE);
        mTitleCustomLayout.setVisibility(View.GONE);

        mTitleTxt.setTextColor(titleTxtColor);

    }

    public void setActions(List<CustomAction> actionMenus) {
        mActions = actionMenus;

        // Action为空的场景
        if (ListUtil.isEmpty(mActions)) {
            mTitleRightBtn.setVisibility(View.GONE);
            mTitleRightImg.setVisibility(View.GONE);
            return;
        }

        // 只有一个Action的场景
        if (mActions.size() == 1) {
            final CustomAction action = mActions.get(0);
            if (action == null) {
                return;
            }

            String rightTxt = action.menuTitle;
            int imgResId = action.imgResId;
            String imgUrl = action.menuImage;

            OnClickListener titleRightclick = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (mActionClickListener != null) {
                            mActionClickListener.onClick(action);
                        }
                    } catch (Exception e) {
                    }
                }
            };

            // 优先设置文字
            if (!TextUtils.isEmpty(rightTxt)) {
                mTitleRightBtn.setText(rightTxt);
                mTitleRightBtn.setVisibility(View.VISIBLE);
                mTitleRightBtn.setOnClickListener(titleRightclick);
                mTitleRightImg.setVisibility(View.GONE);
            } else if (imgResId != 0 || !TextUtils.isEmpty(imgUrl)) {
                mTitleRightBtn.setVisibility(View.GONE);
                mTitleRightImg.setVisibility(View.VISIBLE);
                mTitleRightImg.setOnClickListener(titleRightclick);
                mTitleRightImg.setImageUrl(imgUrl, imgResId);
            } else {
                mTitleRightBtn.setVisibility(View.GONE);
                mTitleRightImg.setVisibility(View.GONE);
            }
            return;
        }

        mActionMenu.setMenuList(mActions);
        mTitleRightBtn.setVisibility(View.GONE);
        mTitleRightImg.setVisibility(View.VISIBLE);
        mTitleRightImg.setImageResource(R.drawable.icon_action_more);

        mTitleRightImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mActionMenu.isShow()) {
                    mActionMenu.dismiss();
                } else {
                    mActionMenu.show(mTitleRightImg);
                }
            }
        });

    }

    /**
     * 设置分割线是否可见（默认不可见）
     *
     * @param enable
     */
    public void setTitleDividerVisiable(boolean enable) {
        if (enable) {
            mTitleDivider.setVisibility(View.VISIBLE);
        } else {
            mTitleDivider.setVisibility(View.GONE);
        }
    }

    /**
     * 设置返回键点击监听
     *
     * @author wyqiuchunlong
     * @param listener
     */
    public void setBackClickListener(OnClickListener listener) {
        if (mTitleLeftImg != null) {
            mTitleLeftImg.setOnClickListener(listener);
        }
    }

    /**
     * 设置左侧图标可见
     *
     * @param visible
     */
    public void setLeftImgVisible(boolean visible) {
        mTitleLeftImg.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置左侧图标图片图片
     *
     * @param url
     *            图片url
     * @param resourceId
     */
    public void setLeftImageUrl(String url, int resourceId) {
        mTitleLeftImg.setImageUrl(url, resourceId);
    }

    /**
     * 设置标题栏左侧图片
     *
     * @param width
     * @param height
     */
    public void setLeftImageSize(int width, int height) {
        setImageSize(width, height, mTitleLeftImg);
    }

    /**
     * 设置标题栏右侧图片
     *
     * @param width
     * @param height
     */
    public void setRightImageSize(int width, int height) {
        setImageSize(width, height, mTitleRightImg);
    }

    /**
     * 设置图片大小
     *
     * @param width
     * @param height
     */
    private void setImageSize(int width, int height, CustomImageView image) {
        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.width = width
                + getResources().getDimensionPixelSize(R.dimen.margin_h_middle)
                * 2;
        params.height = height
                + getResources().getDimensionPixelSize(R.dimen.margin_h_middle)
                * 2;
        image.setLayoutParams(params);
    }

    /**
     * action 点击事件
     *
     * @author yun
     *
     */
    public interface ActionClickListener {
        void onClick(CustomAction menu);
    }

    private ActionClickListener mActionClickListener = null;

    /**
     *
     * @param actionMenuClickListener
     */
    public void setActionClickListener(
            ActionClickListener actionMenuClickListener) {
        if (mActionMenu != null) {
            mActionMenu.setActionClickListener(actionMenuClickListener);
        }
        mActionClickListener = actionMenuClickListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mActionMenu != null) {
            mActionMenu.dismiss();
        }
    }

    public TextView getTitleTxt() {
        return mTitleTxt;
    }

    public CustomButton getTitleRightBtn() {
        return mTitleRightBtn;
    }

    public CustomImageView getTitleRightImg() {
        return mTitleRightImg;
    }

    public CustomImageView getTitleLeftImg() {
        return mTitleLeftImg;
    }

    public View getTitleLayout() {
        return mTitleLayout;
    }

    public FrameLayout getTitleCustomLayout() {
        return mTitleCustomLayout;
    }

    public ViewGroup getTilteBaseLayout() {
        return mTilteBaseLayout;
    }

    public View getTitleDivider() {
        return mTitleDivider;
    }

}
