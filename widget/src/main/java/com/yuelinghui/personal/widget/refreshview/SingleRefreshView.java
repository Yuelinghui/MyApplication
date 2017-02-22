package com.yuelinghui.personal.widget.refreshview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yuelinghui.personal.widget.R;
import com.yuelinghui.personal.widget.recyclerview.BaseRecyclerView;
import com.yuelinghui.personal.widget.recyclerview.LinearRecyclerView;
import com.yuelinghui.personal.widget.recyclerview.Model;
import com.yuelinghui.personal.widget.recyclerview.SingleItemDecoration;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class SingleRefreshView<T extends Model.ItemData, VH extends Model.BaseViewHolder<T>> extends PullToRefreshView {


    public interface IRefreshCallBack {
        void onRefresh();

        void onLoadMore();
    }

    protected LinearRecyclerView<T, VH> mLinearRecyclerView;

    public SingleRefreshView(Context context) {
        this(context, null);
    }

    public SingleRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLinearRecyclerView = new LinearRecyclerView<T, VH>(context, attrs);
        mLinearRecyclerView.setLayoutParams(new ViewGroup.MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mLinearRecyclerView);
        mLinearRecyclerView.setId(R.id.linear_recycler_view);
        buildViewRes(attrs);
    }

    protected void buildViewRes(AttributeSet attrs) {
        mLinearRecyclerView.addItemDecoration(new SingleItemDecoration(getContext(), attrs,mLinearRecyclerView.getFooterViewsCount()));
    }

    public void addData(@NonNull List<T> models) {
        mLinearRecyclerView.add(models);
    }

    public void insertData(@NonNull T data, int index) {
        mLinearRecyclerView.add(data, index);
        mLinearRecyclerView.scrollToPosition(index);
    }

    public void setData(@NonNull List<T> models) {
        mLinearRecyclerView.setDatas(models);
    }

    public void refreshComplete() {
        super.setRefreshing(false);
    }

    public void hasMore(boolean more) {
        if (!more) {
            mLinearRecyclerView.removeFooterView(getFooterView(getContext()));
            mLinearRecyclerView.addFooterView(getNoMoreFooterView(getContext()));
        } else {
            mLinearRecyclerView.removeFooterView(getNoMoreFooterView(getContext()));
            mLinearRecyclerView.addFooterView(getFooterView(getContext()));
        }
    }

    public LinearRecyclerView<T, VH> getLinearRecyclerView() {
        return mLinearRecyclerView;
    }

    private LinearLayout loadMoreView;

    private LinearLayout noMoreView;

    protected ViewGroup getFooterView(Context context) {
        if (loadMoreView == null) {
            loadMoreView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_recycler_footview, null);
            loadMoreView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            loadMoreView.setGravity(Gravity.CENTER);
            GifImageView gifIvFoorView = (GifImageView) loadMoreView.findViewById(R.id.gifIvFoorView);
            gifIvFoorView.setImageResource(R.drawable.icon_loadmore);
        }
        return loadMoreView;
    }

    protected ViewGroup getNoMoreFooterView(Context context) {
        if (noMoreView == null) {
            noMoreView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_recycler_no_more_footer, null);
            noMoreView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            noMoreView.setGravity(Gravity.CENTER);
        }
        return noMoreView;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mLinearRecyclerView.setVisibility(visibility);
    }

    public void setOnRefreshCallBack(final IRefreshCallBack callBack) {
        mLinearRecyclerView.setOnFooterViewBindViewHolderListener(new BaseRecyclerView.OnFooterViewBindViewHolderListener() {
            @Override
            public void onFooterViewBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isInitializeInvoke) {
                callBack.onLoadMore();
            }
        });

        super.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                callBack.onRefresh();
            }
        });
    }
}
