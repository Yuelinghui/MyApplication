package com.yuelinghui.personal.myapplication.home.ui.recyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuelinghui.personal.myapplication.R;
import com.yuelinghui.personal.myapplication.home.entity.StoriesInfo;
import com.yuelinghui.personal.widget.image.CustomImageView;
import com.yuelinghui.personal.widget.recyclerview.Model;
import com.yuelinghui.personal.widget.recyclerview.VHLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuelinghui on 17/2/22.
 */

@VHLayout(layoutId = R.layout.home_list_item_layout)
public class HomeVH extends Model.BaseViewHolder<HomeItemData>{
    @Bind(R.id.layout_item)
    ViewGroup itemLayout;
    @Bind(R.id.txt_title)
    TextView titleTxt;
    @Bind(R.id.txt_multipic)
    TextView multiPicTxt;
    @Bind(R.id.img_logo)
    CustomImageView logoImg;

    public HomeVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void bindData(HomeItemData data) {
        StoriesInfo info = data.getArticleInfo();
        titleTxt.setText(info.title);
        logoImg.setImageUrl(info.images.get(0));
//        itemLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mItemClickListener != null) {
//                    mItemClickListener.onItemClick(info.id);
//                }
//            }
//        });
        if (info.multipic) {
            multiPicTxt.setVisibility(View.VISIBLE);
        } else {
            multiPicTxt.setVisibility(View.GONE);
        }
    }
}
