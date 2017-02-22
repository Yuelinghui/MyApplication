package com.yuelinghui.personal.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class Model {

    public abstract static class BaseViewHolder<T extends ItemData> extends RecyclerView.ViewHolder {
        private static final int DefaultLayoutParamValue = -1384;

        protected Context mContext;

        public static <T extends BaseViewHolder> T newInstance(Class<T> cls, LayoutInflater layoutInflater, ViewGroup viewGroup) {
            T result = null;
            try {
                VHLayout onSub = cls.getAnnotation(VHLayout.class);
                Constructor<T> c1 = cls.getDeclaredConstructor(View.class);
                c1.setAccessible(true);
                result = c1.newInstance(layoutInflater.inflate(onSub.layoutId(), viewGroup, false));
            } catch (Exception e) {
                Log.e("BaseViewHolder", "newInstance", e);
            }
            return result;
        }

        public BaseViewHolder(View itemView) {
            super(itemView);
            if (suggestHeight() != DefaultLayoutParamValue || suggestWidth() != DefaultLayoutParamValue) {
                ViewGroup.LayoutParams lp = itemView.getLayoutParams();
                if (lp != null) {
                    if (suggestHeight() != DefaultLayoutParamValue) {
                        lp.height = suggestHeight();
                    }
                    if (suggestWidth() != DefaultLayoutParamValue) {
                        lp.width = suggestWidth();
                    }
                    itemView.setLayoutParams(lp);
                }
            }
            mContext = itemView.getContext();
        }

        protected int suggestHeight() {
            return DefaultLayoutParamValue;
        }

        protected int suggestWidth() {
            return DefaultLayoutParamValue;
        }

        public abstract void bindData(T data);

        /**********************
         * Adapter触发的生命周期，对应Adapter的同名方法，一定会被调用
         ****************/

        public void onViewBinded() {
        }

        public void onViewRecycled() {
        }

        public void onAttachedToWindow() {
        }

        public void onDetachedFromWindow() {
        }
    }

    public static abstract class ItemData {

    }
}
