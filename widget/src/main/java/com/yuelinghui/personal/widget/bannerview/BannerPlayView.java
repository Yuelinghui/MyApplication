package com.yuelinghui.personal.widget.bannerview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.yuelinghui.personal.maframe.util.ListUtil;
import com.yuelinghui.personal.widget.R;
import com.yuelinghui.personal.widget.fragmentadapter.FragmentDescriptor;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class BannerPlayView extends RelativeLayout {


    /**
     * 默认的自动滚动间隔
     */
    private static final long DELAY_TIME = 5 * 1000L;

    /**
     * 轮播默认的滚动时间
     */
    private static final int SCROLL_DURATION = 700;

    /**
     * 自动轮播handler的msgWhat
     */
    private static final int MSG_WHAT_AUTO_SCROLL = 101;

    /**
     * 初始化ViewPager页面的factor，将ViewPager的初始页加载在bannerListSize乘以Banner的页数的那一页
     */
    private static final int INITIAL_PAGE = 50;

    /**
     * 圆点布局
     */
    private CustomPageControl mPageControl = null;

    /**
     * viewPager主布局
     */
    private ViewPager mMainViewPager = null;

    /**
     * 间隔时间
     */
    private long mIntervalTime = DELAY_TIME;

    /**
     * 自动轮播事件的messageWhat
     *
     */
    private int mAutoScrollEvent = MSG_WHAT_AUTO_SCROLL;

    /**
     * banner列表
     */
    private List<Banner> mBannerList = null;

    private InfiniteFragmentPagerAdapter<Banner> mFragmentAdapter;

    private PagerHandler mPagerHandler;

    private BannerClickListener mBannerClickListener;

    public BannerPlayView(Context context) {
        super(context);
        initView(null);
    }

    public BannerPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    /**
     * 初始化
     *
     * @author wyqiuchunlong
     */
    private void initView(AttributeSet attrs) {
        // 获取自动轮播的messageWhat，因为主页理财和生活界面复用了该控件，所以这里需要用不同的messageWhat区分开来
        Drawable normalPageDot = null;
        Drawable selectedPageDot = null;
        int marginBottom = 0;
        int interval = 0;
        int dotSize = 0;
        String belongType = null;
        int scrollDuration = 0;
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BannerPlayView);
            mAutoScrollEvent = array.getInt(R.styleable.BannerPlayView_autoScrollEvent, MSG_WHAT_AUTO_SCROLL);
            normalPageDot = array.getDrawable(R.styleable.BannerPlayView_pageDrawableNormal);
            selectedPageDot = array.getDrawable(R.styleable.BannerPlayView_pageDrawableSelected);
            marginBottom = array.getDimensionPixelSize(R.styleable.BannerPlayView_pageMarginBottom, 0);
            interval = array.getDimensionPixelSize(R.styleable.BannerPlayView_pageInterval, 0);
            dotSize = array.getDimensionPixelSize(R.styleable.BannerPlayView_pageDotSize, 0);
            belongType = array.getString(R.styleable.BannerPlayView_belongType);
            scrollDuration = array.getInt(R.styleable.BannerPlayView_scrollDuration, SCROLL_DURATION);
            array.recycle();
        }

        mPagerHandler = new PagerHandler(this);
        // 加载布局
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.banner_play_view, this, true);
        // 圆点布局
        mPageControl = (CustomPageControl) findViewById(R.id.page_control);
        mPageControl.setNormalDot(normalPageDot);
        mPageControl.setSelectedDot(selectedPageDot);
        mPageControl.setPageDotInterval(interval);
        mPageControl.setPageDotSize(dotSize);
        if (marginBottom > 0) {
            LayoutParams params = (LayoutParams) mPageControl.getLayoutParams();
            params.bottomMargin = marginBottom;
            mPageControl.setLayoutParams(params);
        }
        // viewpager 主布局
        mMainViewPager = (ViewPager) findViewById(R.id.viewpager_main);
        mMainViewPager.setOffscreenPageLimit(2);
        if (scrollDuration > 0) {
            setScrollerTime(scrollDuration);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(BannerFragment.CLICK_ACTION);
        getContext().registerReceiver(mReceiver,intentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(BannerFragment.CLICK_ACTION)) {
                if (mBannerClickListener != null) {
                    mBannerClickListener.onBannerClick(mPageControl.getPage());
                }
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mReceiver);
    }

    /**
     * 导航栏填充数据
     *
     */
    public void setData(FragmentManager fragmentManager, BannerInfo bannerInfo) {
        boolean result = false;
        long interval = DELAY_TIME;
        if (bannerInfo == null || ListUtil.isEmpty(bannerInfo.bannerData)) {
            generateDefaultBanner();
            result = true;
        } else {
            result = checkBannerList(bannerInfo);
            interval = bannerInfo.interval;
        }
        setIntervalTime(interval);
        if (result) {
            // 显示默认banner，或者bannerSize有变化的时候才需要重新加载ViewPager
            setupViewPager(fragmentManager);
        } else {
            // 只更新数据源
            if (mFragmentAdapter != null) {
                mFragmentAdapter.updateDescriptors(generateDescriptors());
            }
        }
    }

    public void setBannerClickListener(BannerClickListener listener) {
        mBannerClickListener = listener;
    }

    /**
     * 显示默认推荐位
     */
    private void generateDefaultBanner() {
        disableAutoScroll();
        // 生成一个默认banner对象的list，展现
        Banner defaultBanner = new Banner();
        mBannerList = new ArrayList<Banner>();
        mBannerList.add(defaultBanner);
    }

    /**
     * 根据banner数据加载ViewPager
     *
     * @param fragmentManager
     */
    private void setupViewPager(FragmentManager fragmentManager) {
        int bannerSize = ListUtil.size(mBannerList);
        if (bannerSize < 1) {
            return;
        }
        mPageControl.setPageNumber(bannerSize);
        mFragmentAdapter = new InfiniteFragmentPagerAdapter<Banner>(fragmentManager, generateDescriptors());
        mMainViewPager.setAdapter(mFragmentAdapter);
        // 设置初始值
        if (bannerSize > 1) {
            int initialPage = bannerSize * INITIAL_PAGE;
            mMainViewPager.setCurrentItem(initialPage);
        }
        mMainViewPager.setOnPageChangeListener(mOnPageChangeListener);
        enableAutoScroll();
    }

    /**
     * 检查新加载的bannerList与老数据size有无出入，如果有，返回true，表明需要重新加载ViewPager
     * 如果size相同，直接替换List数据即可，注意：size=1的情況比较特殊，直接返回true
     *
     * @param bannerInfo
     * @return
     */
    private boolean checkBannerList(BannerInfo bannerInfo) {
        int newSize = ListUtil.size(bannerInfo.bannerData);
        int oldSize = ListUtil.size(mBannerList);
        boolean compareResult = true;
        if (oldSize == 1 && newSize == 1) {
            // 两种size=1的特殊情况，由于size=1不会轮播，所以需要equals比较
            Banner oldBanner = mBannerList.get(0);
            Banner newBanner = bannerInfo.bannerData.get(0);
            if (oldBanner != null && newBanner != null) {
                compareResult = !oldBanner.equals(newBanner);
            }
        } else {
            compareResult = (newSize != oldSize);
        }
        mBannerList = new ArrayList<Banner>();
        mBannerList.addAll(bannerInfo.bannerData);
        return compareResult;
    }

    /**
     * 根据ADInfo的list生成对应的FragmentDescriptor的list
     *
     * @return
     */
    private List<FragmentDescriptor<Banner>> generateDescriptors() {
        List<FragmentDescriptor<Banner>> descriptors = new ArrayList<FragmentDescriptor<Banner>>();

        for (int i = 0; i < mBannerList.size(); i++) {
            Banner banner = mBannerList.get(i);
            FragmentDescriptor<Banner> descriptor = new FragmentDescriptor<Banner>(BannerFragment.class, banner, i);
            descriptors.add(descriptor);
        }
        return descriptors;
    }

    /**
     * viewpager滑动的监听事件
     */
    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        /**
         * 选中的位置
         */
        @Override
        public void onPageSelected(int index) {
            // 改变圆点选中时的颜色
            mPageControl.setPage(index);
        }

        /**
         * i :当前页面，及你点击滑动的页面
         *
         * f:当前页面偏移的百分比
         *
         * j:当前页面偏移的像素位置
         */
        @Override
        public void onPageScrolled(int i, float f, int j) {
        }

        /**
         * state三种状态 0：什么都没做 1：正在滑动 2：滑动完毕
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                mMainViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                disableAutoScroll();
            } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                enableAutoScroll();
            }
        }
    };

    /**
     * 设置轮播间隔时间
     *
     * @param intervalTime
     */
    private void setIntervalTime(long intervalTime) {
        if (intervalTime < DELAY_TIME) {
            mIntervalTime = DELAY_TIME;
        } else {
            mIntervalTime = intervalTime;
        }
    }

    /**
     * 取消定时轮播
     */
    public void disableAutoScroll() {
        if (mPagerHandler.hasMessages(mAutoScrollEvent)) {
            mPagerHandler.removeMessages(mAutoScrollEvent);
        }
    }

    /**
     * 开启自动轮播
     */
    public void enableAutoScroll() {
        if (ListUtil.size(mBannerList) > 1) {
            disableAutoScroll();
            mPagerHandler.sendEmptyMessageDelayed(mAutoScrollEvent, mIntervalTime);
        }
    }

    /**
     * 获取相应的自动轮播事件
     *
     * @return
     */
    public int getAutoScrollEvent() {
        return mAutoScrollEvent;
    }

    /**
     * 跳转到下一界面
     */
    private void scrollToNextPage() {
        int currentItem = mMainViewPager.getCurrentItem();
        mMainViewPager.setCurrentItem(currentItem + 1, true);
        enableAutoScroll();
    }

    /**
     * 设置轮播滚动时间
     */
    private void setScrollerTime(int scrollerTime) {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            FixSpeedScroller fixScroller = new FixSpeedScroller(getContext(), new DecelerateInterpolator());
            fixScroller.setDuration(scrollerTime);
            scroller.set(mMainViewPager, fixScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class PagerHandler extends Handler {

        private WeakReference<BannerPlayView> reference = null;

        public PagerHandler(BannerPlayView cyclePlayView) {
            this.reference = new WeakReference<BannerPlayView>(cyclePlayView);
        }

        private boolean isActive(Context context) {
            if (context != null) {
                Activity activity = (Activity) context;
                return !activity.isFinishing();
            }
            return false;
        }

        @Override
        public void handleMessage(Message msg) {
            BannerPlayView refView = reference.get();
            if (refView != null && isActive(refView.getContext())) {
                if (msg.what == refView.getAutoScrollEvent()) {
                    refView.scrollToNextPage();
                }
            }
        }

    }

    public interface BannerClickListener {
        void onBannerClick(int index);
    }

}
