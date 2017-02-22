package com.yuelinghui.personal.myapplication.home.ui.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuelinghui.personal.myapplication.R;
import com.yuelinghui.personal.myapplication.core.AppRunningContext;
import com.yuelinghui.personal.widget.core.ui.BaseFragment;
import com.yuelinghui.personal.widget.CustomScrollWebView;
import com.yuelinghui.personal.widget.image.CustomImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuelinghui on 16/10/13.
 */
public class HomeDatailFragment extends BaseFragment {

    @Bind(R.id.img_logo)
    CustomImageView mLogoImg;
    @Bind(R.id.txt_title)
    TextView mTitleTxt;
    @Bind(R.id.txt_source)
    TextView mSourceTxt;
    @Bind(R.id.webview_detail)
    CustomScrollWebView mWebView;

    private DetailData mDetailData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mDetailData = (DetailData) mUIData;
        View view = inflater.inflate(R.layout.detail_fragment_layout, container, false);
        ButterKnife.bind(this, view);

        mLogoImg.setImageUrl(mDetailData.detailInfo.image);
        mTitleTxt.setText(mDetailData.detailInfo.title);
        mSourceTxt.setText(mDetailData.detailInfo.image_source);
        String html = mDetailData.detailInfo.body;
        if (!TextUtils.isEmpty(mDetailData.webCss)) {
            String header = "<style type=\"text/css\">" + mDetailData.webCss + "</style>";
            html = "<html><header>" + header + "</header>" + mDetailData.detailInfo.body + "</body></html>";
        }
        if (AppRunningContext.isNightMode()) {
            html = html.replace("<html", "<html class=\"night\"");
        }
        mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        return view;
    }
}
