package com.yuelinghui.personal.myapplication.home.ui;

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
import com.yuelinghui.personal.myapplication.home.entity.ArticleInfo;
import com.yuelinghui.personal.myapplication.home.entity.StoriesInfo;
import com.yuelinghui.personal.myapplication.home.model.MainModel;
import com.yuelinghui.personal.myapplication.home.ui.detail.HomeDetailActivity;
import com.yuelinghui.personal.myapplication.util.SharedUtil;
import com.yuelinghui.personal.widget.bannerview.Banner;
import com.yuelinghui.personal.widget.bannerview.BannerInfo;
import com.yuelinghui.personal.widget.bannerview.BannerPlayView;
import com.yuelinghui.personal.widget.core.ui.BaseFragment;
import com.yuelinghui.personal.widget.listview.CustomRefreshListView;
import com.yuelinghui.personal.widget.toast.CustomToast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuelinghui on 16/10/13.
 */
public class MainFragment extends BaseFragment {
    public static final String READ_ID = "com.yuelinghui.personal.readId";
    @Bind(R.id.list_main)
    CustomRefreshListView mListView;

    private MainData mMainData;

    private HomeAdapter mHomeAdapter;

    private MainModel mMainModel;

    private BannerPlayView mBannerPlayView;

    private SharedUtil mSharedUtil;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mMainData = (MainData) mUIData;
        View view = inflater.inflate(R.layout.main_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        mListView.setVisibility(View.GONE);
        mSharedUtil = new SharedUtil(mActivity);
        mListView.setLoadEnable(true);
        mListView.setRefreshEnable(false);
        mListView.setOnRefreshListener(mRefreshListener);
        mHomeAdapter = new HomeAdapter(mActivity, null);
        mHomeAdapter.setItemClickListener(mItemClickListener);
        mListView.setAdapter(mHomeAdapter);

        loadLast();
        return view;
    }

    private void initHeaderView() {
        mBannerPlayView = new BannerPlayView(mActivity);
        BannerInfo bannerInfo = new BannerInfo();
        bannerInfo.interval = 5 * 1000;
        bannerInfo.bannerData = new ArrayList<>();
        for (StoriesInfo storiesInfo : mMainData.articleInfo.top_stories) {
            Banner banner = new Banner();
            banner.id = storiesInfo.id;
            banner.imageUrl = storiesInfo.image;
            banner.describe = storiesInfo.title;
            bannerInfo.bannerData.add(banner);
        }
        mBannerPlayView.setData(getChildFragmentManager(), bannerInfo);
        mBannerPlayView.setBannerClickListener(mBannerClickListener);
        mBannerPlayView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.banner_height)));
        mListView.addHeaderView(mBannerPlayView);
    }

    private BannerPlayView.BannerClickListener mBannerClickListener = new BannerPlayView.BannerClickListener() {
        @Override
        public void onBannerClick(int index) {
            StoriesInfo info = mMainData.articleInfo.top_stories.get(index);
            startDetail(info.id);
        }
    };

    private CustomRefreshListView.OnRefreshListener mRefreshListener = new CustomRefreshListView.OnRefreshListener() {
        @Override
        public void onRefresh() {

        }

        @Override
        public void onLoadMore() {
            loadBefore(DateUtil.getYesterDay(mMainData.currentSearchDate));
        }
    };

    private HomeAdapter.ItemClickListener mItemClickListener = new HomeAdapter.ItemClickListener() {
        @Override
        public void onItemClick(int id) {
            startDetail(id);
            mSharedUtil.set(true, id + "");
            mHomeAdapter.notifyDataSetChanged();
        }
    };

    private void startDetail(int id) {
        Intent intent = new Intent();
        intent.setClass(mActivity, HomeDetailActivity.class);
        intent.putExtra(HomeDetailActivity.ARTICLE_ID, id);
        startActivity(intent);
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
                mListView.setVisibility(View.VISIBLE);
                mMainData.currentSearchDate = data.date;
                mMainData.articleInfo = data;
                mHomeAdapter.update(mMainData.articleInfo.stories);
                initHeaderView();
            }

            @Override
            protected void onFailure(int resultCode, String message) {
                CustomToast.makeText(message).show();
            }

            @Override
            protected void onFinish() {
                mListView.commit();
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
                    mListView.setLoadEnable(false);
                    return;
                }
                mMainData.currentSearchDate = data.date;
                mHomeAdapter.add(data.stories);
            }

            @Override
            protected void onFailure(int resultCode, String message) {
                CustomToast.makeText(message).show();
            }

            @Override
            protected void onFinish() {
                mListView.commit();
            }
        });
    }
}
