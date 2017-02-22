package com.yuelinghui.personal.widget.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class BaseRecyclerView extends RecyclerView {
    /**
     * 线性
     */
    public static final int LAYOUT_MANAGER_TYPE_LINEAR = 0;
    /**
     * 网格
     */
    public static final int LAYOUT_MANAGER_TYPE_GRID = 1;
    /**
     * 瀑布流
     */
    public static final int LAYOUT_MANAGER_TYPE_STAGGERED_GRID = 2;

    /**
     * headerView的列表
     */
    private List<View> mHeaderView = new ArrayList<View>();
    /**
     * footerView的列表
     */
    private List<View> mFooterView = new ArrayList<View>();
    /**
     * 所有item的adapter
     */
    private WrapAdapter mWrapAdapter;
    /**
     * 内容显示的adapter
     */
    private Adapter mReqAdapter;
    /**
     * 是否一直显示header或者footer
     */
    private boolean isKeepShowHeadOrFooter = false;
    /**
     * 空白页面id
     */
    private int mEmptyViewResId;
    /**
     * 空白页面
     */
    private View mEmptyView;
    /**
     * header监听
     */
    private OnHeadViewBindViewHolderListener mTempOnHeadViewBindViewHolderListener;
    /**
     * footer监听
     */
    private OnFooterViewBindViewHolderListener mTempOnFooterViewBindViewHolderListener;
    /**
     * LayoutManager的类型
     */
    private int mLayoutManagerType;
    /**
     * 是否显示空白页面
     */
    private boolean hasShowEmptyView = false;


    public BaseRecyclerView(Context context) {
        this(context, null);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mEmptyViewResId != -1) {
            if (null != getParent()) {
                ViewGroup parentView = ((ViewGroup) getParent());
                View tempEmptyView1 = parentView.findViewById(mEmptyViewResId);

                if (null != tempEmptyView1) {
                    mEmptyView = tempEmptyView1;
                    if (isKeepShowHeadOrFooter) {
                        parentView.removeView(tempEmptyView1);
                    }
                } else {
                    ViewParent pParentView = parentView.getParent();
                    if (null != pParentView && pParentView instanceof ViewGroup) {
                        View tempEmptyView2 = ((ViewGroup) pParentView).findViewById(mEmptyViewResId);
                        if (null != tempEmptyView2) {
                            mEmptyView = tempEmptyView2;

                            if (isKeepShowHeadOrFooter) {
                                ((ViewGroup) pParentView).removeView(tempEmptyView2);
                            }
                        }
                    }
                }
            }
            mEmptyViewResId = -1;
        } else if (isKeepShowHeadOrFooter && null != mEmptyView) {
            ((ViewGroup) mEmptyView.getParent()).removeView(mEmptyView);
        }

        if (null == adapter) {
            if (null != mReqAdapter) {
                if (!isKeepShowHeadOrFooter) {
                    mReqAdapter.unregisterAdapterDataObserver(mReqAdapterDataObserver);
                }
                mReqAdapter = null;
                mWrapAdapter = null;

                processEmptyView();
            }

            return;
        }

        mReqAdapter = adapter;
        mWrapAdapter = new WrapAdapter(this, adapter, mHeaderView, mFooterView, mLayoutManagerType);

        mWrapAdapter.setOnHeadViewBindViewHolderListener(mTempOnHeadViewBindViewHolderListener);
        mWrapAdapter.setOnFooterViewBindViewHolderListener(mTempOnFooterViewBindViewHolderListener);

        mReqAdapter.registerAdapterDataObserver(mReqAdapterDataObserver);
        super.setAdapter(mWrapAdapter);

        processEmptyView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mReqAdapter && mReqAdapter.hasObservers()) {
            mReqAdapter.unregisterAdapterDataObserver(mReqAdapterDataObserver);
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);

        if (null == layout) return ;
        if (layout instanceof GridLayoutManager) {
            mLayoutManagerType = LAYOUT_MANAGER_TYPE_GRID;
        } else if (layout instanceof StaggeredGridLayoutManager) {
            mLayoutManagerType = LAYOUT_MANAGER_TYPE_STAGGERED_GRID;
        } else if (layout instanceof LinearLayoutManager) {
            mLayoutManagerType = LAYOUT_MANAGER_TYPE_LINEAR;
        }
        if (mWrapAdapter != null) {
            mWrapAdapter.setLayoutManagerType(mLayoutManagerType);
        }
    }

    public void setEmptyView(View emptyView) {
        setEmptyView(emptyView, false);
    }

    public void setEmptyView(View emptyView, boolean isKeepShowHeadOrFooter) {
        this.mEmptyView = emptyView;
        this.isKeepShowHeadOrFooter = isKeepShowHeadOrFooter;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setEmptyViewKeepShowHeadOrFooter(boolean isKeepShowHeadOrFoot) {
        this.isKeepShowHeadOrFooter = isKeepShowHeadOrFoot;
    }

    public boolean isKeepShowHeadOrFooter() {
        return isKeepShowHeadOrFooter;
    }

    public boolean isShowEmptyView() {
        return hasShowEmptyView;
    }

    public void setOnHeadViewBindViewHolderListener(OnHeadViewBindViewHolderListener onHeadViewBindViewHolderListener) {
        if (null != mWrapAdapter) {
            mWrapAdapter.setOnHeadViewBindViewHolderListener(onHeadViewBindViewHolderListener);
        } else {
            this.mTempOnHeadViewBindViewHolderListener = onHeadViewBindViewHolderListener;
        }
    }

    /**
     * FooterView onBindViewHolder callback
     * @param onFooterViewBindViewHolderListener OnFooterViewBindViewHolderListener
     */
    public void setOnFooterViewBindViewHolderListener(OnFooterViewBindViewHolderListener onFooterViewBindViewHolderListener) {
        if (null != mWrapAdapter) {
            mWrapAdapter.setOnFooterViewBindViewHolderListener(onFooterViewBindViewHolderListener);
        } else {
            this.mTempOnFooterViewBindViewHolderListener = onFooterViewBindViewHolderListener;
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, false);
    }

    public void addHeaderView(View v, boolean isScrollTo) {
        if (mHeaderView.contains(v)) return;

        mHeaderView.add(v);
        if (null != mWrapAdapter) {
            int pos = mHeaderView.size() - 1;
            mWrapAdapter.notifyItemInserted(pos);

            if (isScrollTo) {
                scrollToPosition(pos);
            }
        }
    }

    public boolean removeHeaderView(View v) {
        if (!mHeaderView.contains(v)) return false;

        if (null != mWrapAdapter) {
            mWrapAdapter.notifyItemRemoved(mHeaderView.indexOf(v));
        }
        return mHeaderView.remove(v);
    }

    public void addFooterView(View v) {
        addFooterView(v, false);
    }

    public void addFooterView(View v, boolean isScrollTo) {
        if (mFooterView.contains(v)) return;

        mFooterView.add(v);
        if (null != mWrapAdapter) {
            int pos = (null == mReqAdapter ? 0 : mReqAdapter.getItemCount()) + getHeaderViewsCount() + mFooterView.size() - 1;
            mWrapAdapter.notifyItemInserted(pos);
            if (isScrollTo) {
                scrollToPosition(pos);
            }
        }
    }

    public boolean removeFooterView(View v) {
        if (!mFooterView.contains(v)) return false;

        if (null != mWrapAdapter) {
            int pos = (null == mReqAdapter ? 0 : mReqAdapter.getItemCount()) + getHeaderViewsCount() + mFooterView.indexOf(v);
            mWrapAdapter.notifyItemRemoved(pos);
        }
        return mFooterView.remove(v);
    }

    public int getHeaderViewsCount() {
        return mHeaderView.size();
    }

    public int getFooterViewsCount() {
        return mFooterView.size();
    }

    private void processEmptyView() {
        if (null != mEmptyView) {
            boolean isShowEmptyView = (null != mReqAdapter ? mReqAdapter.getItemCount() : 0) == 0;

            if (isShowEmptyView == hasShowEmptyView) return ;

            if (isKeepShowHeadOrFooter) {
                if (hasShowEmptyView) {
                    mWrapAdapter.notifyItemRemoved(getHeaderViewsCount());
                }
            } else {
                mEmptyView.setVisibility(isShowEmptyView ? VISIBLE : GONE);
                setVisibility(isShowEmptyView ? GONE : VISIBLE);
            }

            this.hasShowEmptyView = isShowEmptyView;
        }
    }


    private AdapterDataObserver mReqAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
            processEmptyView();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemInserted(getHeaderViewsCount() + positionStart);
            processEmptyView();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRemoved(getHeaderViewsCount() + positionStart);
            processEmptyView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(getHeaderViewsCount() + positionStart, itemCount);
            processEmptyView();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(getHeaderViewsCount() + fromPosition, getHeaderViewsCount() + toPosition);
        }
    };

    public static class WrapAdapter extends RecyclerView.Adapter {

        private static final int VIEW_TYPE_HEADER = -1;
        private static final int VIEW_TYPE_FOOTER = -2;
        private static final int VIEW_TYPE_EMPTY_VIEW = -3;

        private List<View> mHeaderView;
        private List<View> mFooterView;
        private RecyclerView.Adapter mReqAdapter;
        private int curHeaderOrFooterPos;
        private BaseRecyclerView mBaseRecyclerView;
        private OnHeadViewBindViewHolderListener mOnHeadViewBindViewHolderListener;
        private OnFooterViewBindViewHolderListener mOnFooterViewBindViewHolderListener;
        private List<Integer> mHeadOrFooterInitInvokeViewBindViewFlag = new ArrayList<>();

        private int mLayoutManagerType;

        public WrapAdapter(BaseRecyclerView baseRecyclerView, RecyclerView.Adapter reqAdapter, List<View> mHeaderView, List<View> mFooterView, int layoutManagerType) {
            this.mBaseRecyclerView = baseRecyclerView;
            this.mReqAdapter = reqAdapter;
            this.mHeaderView = mHeaderView;
            this.mFooterView = mFooterView;
            this.mLayoutManagerType = layoutManagerType;
        }

        public int getHeadersCount() {
            return null != mHeaderView ? mHeaderView.size() : 0;
        }

        public int getFootersCount() {
            return null != mFooterView ? mFooterView.size() : 0;
        }

        private int getReqAdapterCount() {
            return null != mReqAdapter ? mReqAdapter.getItemCount() : 0;
        }

        public void setLayoutManagerType(int mLayoutManagerType) {
            this.mLayoutManagerType = mLayoutManagerType;
        }

        @Override
        public int getItemCount() {
            int count = 0;
            int tempItemCount = mReqAdapter.getItemCount();
            if (mBaseRecyclerView.isKeepShowHeadOrFooter()) {
                count += tempItemCount == 0 ? 1 : tempItemCount;
            } else {
                count += tempItemCount;
            }

            if (null != mHeaderView && mHeaderView.size() > 0) {
                count += mHeaderView.size();
            }

            if (null != mFooterView && mFooterView.size() > 0) {
                count += mFooterView.size();
            }

            return count;
        }

        @Override
        public int getItemViewType(int position) {
            // header view
            if (isHeaderView(position)) {
                this.curHeaderOrFooterPos = position;
                return VIEW_TYPE_HEADER;
            }

            int headersCount = getHeadersCount();
            // item view
            int adapterCount = 0;
            if (getReqAdapterCount() > 0 && position >= headersCount) {
                int adjPosition = position - headersCount;
                adapterCount = mReqAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mReqAdapter.getItemViewType(adjPosition);
                }
            } else if (mBaseRecyclerView.isKeepShowHeadOrFooter()) {
                // empty view
                if (position == headersCount) return VIEW_TYPE_EMPTY_VIEW;
            }

            // footer view
            this.curHeaderOrFooterPos = position - headersCount - adapterCount;
            return VIEW_TYPE_FOOTER;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_HEADER: {
                    // create header view
                    EmptyHeaderOrFooterViewHolder headerViewHolder;
                    View tempHeadView = mHeaderView.get(curHeaderOrFooterPos);
                    if (mLayoutManagerType == BaseRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
                        FrameLayout mContainerView = new FrameLayout(tempHeadView.getContext());
                        if (tempHeadView.getParent() != null) {
                            if (tempHeadView.getParent() instanceof FrameLayout) {
                                FrameLayout parentLayout = (FrameLayout) tempHeadView.getParent();
                                parentLayout.removeView(tempHeadView);
                            }
                        }
                        mContainerView.addView(tempHeadView);
                        headerViewHolder = new EmptyHeaderOrFooterViewHolder(mContainerView);
                        StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setFullSpan(true);
                        headerViewHolder.itemView.setLayoutParams(layoutParams);
                    } else {
                        headerViewHolder = new EmptyHeaderOrFooterViewHolder(tempHeadView);
                    }

                    headerViewHolder.setIsRecyclable(false);

                    return headerViewHolder;
                }
                case VIEW_TYPE_FOOTER: {
                    // create footer view
                    EmptyHeaderOrFooterViewHolder footerViewHolder;
                    View tempFooterView;

                    // fix fast delete IndexOutOfBoundsException
                    int footerViewCount = mFooterView.size();
                    if (curHeaderOrFooterPos >= footerViewCount) {
                        curHeaderOrFooterPos = footerViewCount - 1;
                    }

                    tempFooterView = mFooterView.get(curHeaderOrFooterPos);
                    ViewParent tempFooterViewParent = tempFooterView.getParent();
                    if (null != tempFooterViewParent) {
                        ((ViewGroup) tempFooterViewParent).removeView(tempFooterView);
                    }

                    if (mLayoutManagerType == BaseRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
                        FrameLayout mContainerView = new FrameLayout(tempFooterView.getContext());
                        mContainerView.addView(tempFooterView);
                        footerViewHolder = new EmptyHeaderOrFooterViewHolder(mContainerView);
                        StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setFullSpan(true);
                        footerViewHolder.itemView.setLayoutParams(layoutParams);
                    } else {
                        footerViewHolder = new EmptyHeaderOrFooterViewHolder(tempFooterView);
                    }

                    // fix multiple footerView disorder
                    if (mFooterView.size() > 2) {
                        footerViewHolder.setIsRecyclable(false);
                    }

                    return footerViewHolder;
                }
                case VIEW_TYPE_EMPTY_VIEW: {
                    EmptyHeaderOrFooterViewHolder emptyViewHolder;
                    View emptyView = mBaseRecyclerView.getEmptyView();
                    emptyView.setVisibility(View.VISIBLE);
                    if (mLayoutManagerType == BaseRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
                        FrameLayout mContainerView = new FrameLayout(emptyView.getContext());
                        mContainerView.addView(emptyView);
                        emptyViewHolder = new EmptyHeaderOrFooterViewHolder(mContainerView);
                        StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setFullSpan(true);
                        emptyViewHolder.itemView.setLayoutParams(layoutParams);
                    } else {
                        emptyViewHolder = new EmptyHeaderOrFooterViewHolder(emptyView);
                    }

                    return emptyViewHolder;
                }
                default:
                    return mReqAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();

            if (mReqAdapter != null && !(holder instanceof EmptyHeaderOrFooterViewHolder) && !isHeaderView(position) && !isFooterView(position)) {
                mReqAdapter.onViewAttachedToWindow(holder);
            }
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (mReqAdapter != null && !(holder instanceof EmptyHeaderOrFooterViewHolder) && !isHeaderView(position) && !isFooterView(position)) {
                mReqAdapter.onViewDetachedFromWindow(holder);
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);

            if (null == mReqAdapter) {
                return;
            }

            mReqAdapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);

            if (null == mReqAdapter) {
                return;
            }

            mReqAdapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (mReqAdapter != null && !(holder instanceof EmptyHeaderOrFooterViewHolder) && !isHeaderView(position) && !isFooterView(position)) {
                mReqAdapter.onViewRecycled(holder);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int curItemViewType = getItemViewType(position);

            if (curItemViewType == VIEW_TYPE_HEADER && null != mOnHeadViewBindViewHolderListener) {
                // head view
                boolean isInitializeInvoke = false;
                int tempHeadViewHashCode = holder.itemView.hashCode();
                if (!mHeadOrFooterInitInvokeViewBindViewFlag.contains(tempHeadViewHashCode)) {
                    mHeadOrFooterInitInvokeViewBindViewFlag.add(tempHeadViewHashCode);
                    isInitializeInvoke = true;
                }
                mOnHeadViewBindViewHolderListener.onHeadViewBindViewHolder(holder, position, isInitializeInvoke);
            } else if (curItemViewType == VIEW_TYPE_FOOTER && null != mOnFooterViewBindViewHolderListener) {
                // footer view
                boolean isInitializeInvoke = false;
                int tempFooterViewHashCode = holder.itemView.hashCode();
                if (!mHeadOrFooterInitInvokeViewBindViewFlag.contains(tempFooterViewHashCode)) {
                    mHeadOrFooterInitInvokeViewBindViewFlag.add(tempFooterViewHashCode);
                    isInitializeInvoke = true;
                }
                mOnFooterViewBindViewHolderListener.onFooterViewBindViewHolder(holder, position - getHeadersCount() - getReqAdapterCount(), isInitializeInvoke);
            } else if (curItemViewType >= 0) {
                // item view
                final int adjPosition = position - getHeadersCount();
                int adapterCount;
                if (mReqAdapter != null) {
                    adapterCount = mReqAdapter.getItemCount();
                    if (adjPosition < adapterCount) {
                        mReqAdapter.onBindViewHolder(holder, adjPosition);
                    }
                }
            }
        }

        public void setOnHeadViewBindViewHolderListener(OnHeadViewBindViewHolderListener mOnHeadViewBindViewHolderListener) {
            this.mOnHeadViewBindViewHolderListener = mOnHeadViewBindViewHolderListener;
        }

        public void setOnFooterViewBindViewHolderListener(OnFooterViewBindViewHolderListener mOnFooterViewBindViewHolderListener) {
            this.mOnFooterViewBindViewHolderListener = mOnFooterViewBindViewHolderListener;
        }

        private boolean isHeaderView(int position) {
            return position < getHeadersCount();
        }

        private boolean isFooterView(int position) {
            return getFootersCount() > 0 && position - getHeadersCount() - getReqAdapterCount() >= 0;
        }

        class EmptyHeaderOrFooterViewHolder extends RecyclerView.ViewHolder {
            public EmptyHeaderOrFooterViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public interface OnHeadViewBindViewHolderListener {
        void onHeadViewBindViewHolder(ViewHolder holder, int position, boolean isInitializeInvoke);
    }

    public interface OnFooterViewBindViewHolderListener {
        void onFooterViewBindViewHolder(ViewHolder holder, int position, boolean isInitializeInvoke);
    }
}
