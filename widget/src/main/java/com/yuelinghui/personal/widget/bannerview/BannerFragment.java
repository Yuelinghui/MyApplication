package com.yuelinghui.personal.widget.bannerview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuelinghui.personal.widget.R;
import com.yuelinghui.personal.widget.fragmentadapter.CustomPagerFragment;
import com.yuelinghui.personal.widget.image.CustomImageView;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class BannerFragment extends CustomPagerFragment {

    public static final String CLICK_ACTION = "com.yuelinghui.personal.bannerClick";

    /**
     * 推荐位配置信息
     */
    private Banner mBanner;

    private CustomImageView mImageView;

    private TextView mDescriptTxt;

    private int mPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.banner_fragment_layout, container,
                false);

        mImageView = (CustomImageView) view.findViewById(R.id.imgview_banner);
        mDescriptTxt = (TextView) view.findViewById(R.id.txt_descript);

        mBanner = (Banner) getArguments().getSerializable(ARG_INFO);
        // 默认从1开始计数
        mPosition = getArguments().getInt(ARG_POSITION, 0) + 1;

        if (mBanner != null) {
            mImageView.setImageUrl(mBanner.imageUrl);
            mDescriptTxt.setText(mBanner.describe);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CLICK_ACTION);
                    mActivity.sendBroadcast(intent);
                }
            });
        }

        return view;
    }

}
