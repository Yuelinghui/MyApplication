package com.yuelinghui.personal.widget.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by yuelinghui on 17/2/22.
 */

public abstract class VHGenerator<T extends Model.ItemData, VH extends Model.BaseViewHolder<T>> {
    protected final LayoutInflater mLayoutInflater;

    public VHGenerator(LayoutInflater layoutInflater) {
        this.mLayoutInflater = layoutInflater;
    }

    public VH buildViewHolder(ViewGroup parent, int itemType) {
        return Model.BaseViewHolder.newInstance(getViewHolderClass(itemType), mLayoutInflater, parent);
    }

    public abstract Class<? extends VH> getViewHolderClass(int itemType);

    public abstract int getItemType(T baseData);
}
