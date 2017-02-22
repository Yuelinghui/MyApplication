package com.yuelinghui.personal.myapplication.home.ui.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuelinghui.personal.maframe.result.Result;
import com.yuelinghui.personal.maframe.result.ResultHandler;
import com.yuelinghui.personal.maframe.util.DateUtil;
import com.yuelinghui.personal.myapplication.R;
import com.yuelinghui.personal.myapplication.core.AppRunningContext;
import com.yuelinghui.personal.widget.core.ui.BaseFragment;
import com.yuelinghui.personal.myapplication.home.entity.ArticleInfo;
import com.yuelinghui.personal.myapplication.home.entity.StoriesInfo;
import com.yuelinghui.personal.myapplication.home.model.MainModel;
import com.yuelinghui.personal.myapplication.home.ui.MainData;
import com.yuelinghui.personal.myapplication.home.ui.detail.HomeDetailActivity;
import com.yuelinghui.personal.widget.recyclerview.LinearRecyclerView;
import com.yuelinghui.personal.widget.recyclerview.Model;
import com.yuelinghui.personal.widget.refreshview.SingleRefreshView;
import com.yuelinghui.personal.widget.toast.CustomToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class RecyclerFragment extends BaseFragment{
    @Bind(R.id.refresh_view)
    SingleRefreshView<HomeItemData,Model.BaseViewHolder<HomeItemData>> mRefreshView;

    private MainData mMainData;

    private MainModel mMainModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_fragment_layout,container,false);
        ButterKnife.bind(this,view);
        mMainData = (MainData) mUIData;
        mRefreshView.getLinearRecyclerView().setGenerator(new HomeVHGennerator(LayoutInflater.from(getActivity())));
        mRefreshView.setOnRefreshCallBack(new SingleRefreshView.IRefreshCallBack() {
            @Override
            public void onRefresh() {
                loadLast();
            }

            @Override
            public void onLoadMore() {
                loadBefore(DateUtil.getYesterDay(mMainData.currentSearchDate));
            }
        });
        mRefreshView.getLinearRecyclerView().setOnItemClickListener(new LinearRecyclerView.OnItemClickListener<HomeItemData>() {
            @Override
            public void onItemClick(LinearRecyclerView recyclerView, View view, int position, HomeItemData model) {
                startDetail(model.getArticleInfo().id);
            }
        });

        loadLast();
        return view;
    }

    private void loadLast() {
        if (mMainModel == null) {
            mMainModel = new MainModel(mActivity);
        }

        mMainModel.queryLast(new ResultHandler<ArticleInfo>() {
            @Override
            protected boolean onStart() {
                return AppRunningContext.checkNetWork();
            }

            @Override
            protected void onSuccess(ArticleInfo data, String message) {
                if (data == null) {
                    onFailure(Result.ERROR, "数据错误");
                    return;
                }
                mRefreshView.setVisibility(View.VISIBLE);
                mMainData.currentSearchDate = data.date;
                mMainData.articleInfo = data;
                mRefreshView.setData(convertData(data.stories));
                mRefreshView.hasMore(true);
            }

            @Override
            protected void onFailure(int resultCode, String message) {
                CustomToast.makeText(message).show();
            }

            @Override
            protected void onFinish() {
                mRefreshView.refreshComplete();
            }
        });
    }

    private void loadBefore(String date) {
        if (mMainModel == null) {
            mMainModel = new MainModel(mActivity);
        }
        mMainModel.queryBefore(date, new ResultHandler<ArticleInfo>() {
            @Override
            protected boolean onStart() {
                return AppRunningContext.checkNetWork();
            }

            @Override
            protected void onSuccess(ArticleInfo data, String message) {
                if (data == null) {
                    onFailure(Result.ERROR, "数据错误");
                    mRefreshView.hasMore(false);
                    return;
                }
                mMainData.currentSearchDate = data.date;
                mRefreshView.addData(convertData(data.stories));
            }

            @Override
            protected void onFailure(int resultCode, String message) {
                CustomToast.makeText(message).show();
            }

            @Override
            protected void onFinish() {
                mRefreshView.refreshComplete();
            }
        });
    }

    private void startDetail(int id) {
        Intent intent = new Intent();
        intent.setClass(mActivity, HomeDetailActivity.class);
        intent.putExtra(HomeDetailActivity.ARTICLE_ID, id);
        startActivity(intent);
    }

    protected ArrayList<HomeItemData> convertData(List<StoriesInfo> info) {
        ArrayList<HomeItemData> inserFeeds = new ArrayList<>();
        for (StoriesInfo storiesInfo : info) {
            HomeItemData homeItemData = new HomeItemData(storiesInfo);
            inserFeeds.add(homeItemData);
        }
        return inserFeeds;
    }
}
