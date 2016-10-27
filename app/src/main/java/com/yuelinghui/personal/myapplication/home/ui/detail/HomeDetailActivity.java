package com.yuelinghui.personal.myapplication.home.ui.detail;

import android.os.Bundle;

import com.yuelinghui.personal.maframe.UIData;
import com.yuelinghui.personal.maframe.result.Result;
import com.yuelinghui.personal.maframe.result.ResultHandler;
import com.yuelinghui.personal.maframe.util.ListUtil;
import com.yuelinghui.personal.myapplication.R;
import com.yuelinghui.personal.myapplication.core.AppRunningContext;
import com.yuelinghui.personal.myapplication.home.entity.HomeDetailInfo;
import com.yuelinghui.personal.myapplication.home.model.MainModel;
import com.yuelinghui.personal.widget.core.RunningContext;
import com.yuelinghui.personal.widget.core.WebTextManager;
import com.yuelinghui.personal.widget.core.ui.BaseActivity;
import com.yuelinghui.personal.widget.toast.CustomToast;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class HomeDetailActivity extends BaseActivity {

    public static final String ARTICLE_ID = "com.yuelinghui.personal.homeDetail.ARTICLE_ID";
    private MainModel mMainModel;

    private DetailData mDetailData;

    private WebTextManager mTextManager;

    private int mArticleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewAndTitle(FRAGMENT_LAYOUT,getString(R.string.detail_title));
        if (AppRunningContext.isNightMode()) {
            setTitleBarColor(getResources().getColor(R.color.night_mode_background));
        }
        mDetailData = (DetailData) mUIData;
        mArticleId = getIntent().getIntExtra(ARTICLE_ID,-1);
        if (mArticleId == -1) {
            CustomToast.makeText(getString(R.string.extra_error)).show();
            return;
        }
        if (savedInstanceState == null) {
            load();
        }
    }

    @Override
    protected void load() {
        if (mMainModel == null) {
            mMainModel = new MainModel(this);
        }
        mMainModel.querDetail(mArticleId, new ResultHandler<HomeDetailInfo>() {
            @Override
            protected boolean onStart() {
                return showNetProgress(null,this);
            }

            @Override
            protected void onSuccess(HomeDetailInfo data, String message) {
                if (data == null) {
                    onFailure(Result.ERROR,"数据错误");
                    return;
                }
                mDetailData.detailInfo = data;
                if (ListUtil.isEmpty(mDetailData.detailInfo.css)) {
                    startFirstFragment(new HomeDatailFragment());
                } else {
                    loadCss();
                }
            }

            @Override
            protected void onFailure(int resultCode, String message) {
                CustomToast.makeText(message).show();
            }

            @Override
            protected void onFinish() {
                dismissProgress();
            }
        });
    }

    private void loadCss() {
        if (mTextManager == null) {
            mTextManager = new WebTextManager(this);
        }
        mTextManager.loadWebText(mDetailData.detailInfo.css.get(0), new ResultHandler<String>() {
            @Override
            protected boolean onStart() {
                return RunningContext.checkNetWork();
            }

            @Override
            protected void onSuccess(String data, String message) {
                mDetailData.webCss = data;
            }

            @Override
            protected void onFinish() {
                startFirstFragment(new HomeDatailFragment());
            }
        });
    }

    @Override
    protected UIData initUIData() {
        return new DetailData();
    }
}
