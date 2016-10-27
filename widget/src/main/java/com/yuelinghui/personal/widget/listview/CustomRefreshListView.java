package com.yuelinghui.personal.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Scroller;

import java.text.SimpleDateFormat;

/**
 * Created by yuelinghui on 16/8/19.
 */
public class CustomRefreshListView extends ListView implements AbsListView.OnScrollListener{

    /**
     * 布局最小展示时间
     */
    private static final long MIN_DISPLAY_TIME = 1000;

    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int PULL_DELTA = 50;
    private final static float OFFSET_RADIO = 1.8f;
    private final static int SCROLL_DURATION = 400;

    private CustomRefreshListViewHeader mHeaderView;
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false;

    private CustomRefreshListViewFooter mFooterView;
    private boolean mEnableLoadMore = true;
    private boolean mPullLoading = false;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss");
    /**
     * 上次刷新时间
     */
    private long mRefreshDate = 0;

    private float mLastY = -1;
    private Scroller mScroller;

    private OnRefreshListener mRefreshListener;
    private int mTotalItemCount;
    private int mScrollBack;

    public CustomRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public CustomRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomRefreshListView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mScroller = new Scroller(context,new DecelerateInterpolator());
        super.setOnScrollListener(this);

        mHeaderView = new CustomRefreshListViewHeader(context);
        addHeaderView(mHeaderView);
        mHeaderView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startRefresh();
            }
        });

        mFooterView = new CustomRefreshListViewFooter(context);
        addFooterView(mFooterView);
        mFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadMore();
            }
        });
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (mEnablePullRefresh) {
            mHeaderView.show();
            mHeaderView.setState(CustomRefreshListViewHeader.STATE_NORMAL);
            mPullRefreshing = false;
        } else {
           mHeaderView.hide();
        }
    }

    public void setLoadEnable(boolean enable) {
        mEnableLoadMore = enable;
        if (mEnableLoadMore) {
            mFooterView.show();
            mFooterView.setState(CustomRefreshListViewFooter.STATE_NORMAL);
            mPullLoading = false;
        } else {
            mFooterView.hide();
        }
    }

    public void commit() {
        long delay = 0;
        if (mRefreshDate != 0) {
            long gap = System.currentTimeMillis() - mRefreshDate;
            if (gap < MIN_DISPLAY_TIME) {
                delay = MIN_DISPLAY_TIME - gap;
            }
        }
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                CustomRefreshListView.this.stopRefresh();
                CustomRefreshListView.this.stopLoadMore();

            }
        },delay);
    }

    private void updateRefreshTime() {
        mRefreshDate = System.currentTimeMillis();
    }

    public void startRefresh() {
        mPullRefreshing = true;
        mHeaderView.setState(CustomRefreshListViewHeader.STATE_REFRESHING);
        if (mRefreshListener != null) {
            updateRefreshTime();
            mRefreshListener.onRefresh();
        }
    }

    public void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(CustomRefreshListViewFooter.STATE_LOADING);
        if (mRefreshListener != null) {
            updateRefreshTime();
            mRefreshListener.onLoadMore();
        }

    }

    public void stopRefresh() {
        if (mPullRefreshing) {
            mPullRefreshing = false;
            mHeaderView.setState(CustomRefreshListViewHeader.STATE_NORMAL);
        }
    }

    public void stopLoadMore() {
        if (mPullLoading) {
            mPullLoading = false;
            mFooterView.setState(CustomRefreshListViewFooter.STATE_NORMAL);
        }
    }

    private void updateHeaderHeight(float delta) {
        int height = mHeaderView.getTopMargin() + (int) delta;
        if (mEnablePullRefresh && !mPullRefreshing) {
            if (height > PULL_DELTA) { // height enough to invoke load
                mHeaderView.setState(CustomRefreshListViewFooter.STATE_READY);
            } else {
                mHeaderView.setState(CustomRefreshListViewFooter.STATE_NORMAL);
            }
        }
        mHeaderView.setTopMargin(height);
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnableLoadMore && !mPullLoading) {
            if (height > PULL_DELTA) { // height enough to invoke load
                mFooterView.setState(CustomRefreshListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(CustomRefreshListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);
    }

    private void resetHeaderHeight() {
        int topMargin = mHeaderView.getTopMargin();
        if (topMargin > 0) {
            mScrollBack = SCROLLBACK_HEADER;
            mScroller.startScroll(0, topMargin, 0, -topMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }
    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setTopMargin(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }

            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 判断listView是否空闲, 空闲则允许load、refresh
     *
     * @return
     */
    private boolean isIdle() {
        return !mPullLoading && !mPullRefreshing;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0
                        && (mHeaderView.getTopMargin() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                } else if (getLastVisiblePosition() == mTotalItemCount - 1
                        && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh && isIdle()
                            && mHeaderView.getTopMargin() > PULL_DELTA) {
                        startRefresh();
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnableLoadMore && isIdle()
                            && mFooterView.getBottomMargin() > PULL_DELTA) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mTotalItemCount = totalItemCount;
    }

    public interface OnRefreshListener {
        void onRefresh();
        void onLoadMore();
    }
}
