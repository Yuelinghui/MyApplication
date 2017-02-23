package com.yuelinghui.personal.myapplication.home.ui.recyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuelinghui.personal.myapplication.R;
import com.yuelinghui.personal.myapplication.core.AppRunningContext;
import com.yuelinghui.personal.myapplication.home.entity.StoriesInfo;
import com.yuelinghui.personal.myapplication.util.SharedUtil;
import com.yuelinghui.personal.widget.image.CustomImageView;
import com.yuelinghui.personal.widget.recyclerview.Model;
import com.yuelinghui.personal.widget.recyclerview.VHLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuelinghui on 17/2/22.
 */

@VHLayout(layoutId = R.layout.home_recycler_item_layout)
public class HomeVH extends Model.BaseViewHolder<HomeItemData>{
    @Bind(R.id.layout_item)
    ViewGroup itemLayout;
    @Bind(R.id.txt_title)
    TextView titleTxt;
    @Bind(R.id.txt_multipic)
    TextView multiPicTxt;
    @Bind(R.id.img_logo)
    CustomImageView logoImg;

    private SharedUtil mShareUtil;

    public HomeVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        mShareUtil = new SharedUtil(mContext);
    }

    @Override
    public void bindData(HomeItemData data) {
        StoriesInfo info = data.getArticleInfo();
        Boolean readId = mShareUtil.get(Boolean.class, info.id + "");
        if (!AppRunningContext.isNightMode()) {
            if (readId != null) {
                if (readId) {
                    titleTxt.setTextColor(mContext.getResources().getColor(R.color.bg_pressed));
                } else {
                    titleTxt.setTextColor(mContext.getResources().getColor(R.color.txt_main));
                }
            } else {
                titleTxt.setTextColor(mContext.getResources().getColor(R.color.txt_main));
            }
        } else {
            if (readId != null) {
                if (readId) {
                    titleTxt.setTextColor(mContext.getResources().getColor(R.color.night_mode_item_read));
                } else {
                    titleTxt.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            } else {
                titleTxt.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }
        titleTxt.setText(info.title);
        logoImg.setImageUrl(info.images.get(0));
        if (info.multipic) {
            multiPicTxt.setVisibility(View.VISIBLE);
        } else {
            multiPicTxt.setVisibility(View.GONE);
        }
    }
}
