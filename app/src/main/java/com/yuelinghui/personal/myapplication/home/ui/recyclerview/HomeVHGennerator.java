package com.yuelinghui.personal.myapplication.home.ui.recyclerview;

import android.view.LayoutInflater;

import com.yuelinghui.personal.widget.recyclerview.Model;
import com.yuelinghui.personal.widget.recyclerview.VHGenerator;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class HomeVHGennerator extends VHGenerator<HomeItemData, Model.BaseViewHolder<HomeItemData>> {
    public HomeVHGennerator(LayoutInflater layoutInflater) {
        super(layoutInflater);
    }

    @Override
    public Class<? extends Model.BaseViewHolder<HomeItemData>> getViewHolderClass(int itemType) {
        return HomeVH.class;
    }

    @Override
    public int getItemType(HomeItemData baseData) {
        return 0;
    }
}
