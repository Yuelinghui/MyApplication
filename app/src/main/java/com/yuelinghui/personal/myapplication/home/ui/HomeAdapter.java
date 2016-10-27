package com.yuelinghui.personal.myapplication.home.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuelinghui.personal.myapplication.R;
import com.yuelinghui.personal.myapplication.core.AppRunningContext;
import com.yuelinghui.personal.myapplication.home.entity.StoriesInfo;
import com.yuelinghui.personal.myapplication.util.SharedUtil;
import com.yuelinghui.personal.widget.image.CustomImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class HomeAdapter extends BaseAdapter {

    private List<StoriesInfo> mStoriesList;
    private Context mContext;
    private ItemClickListener mItemClickListener;
    private SharedUtil mShareUtil;

    public HomeAdapter(Context context, List<StoriesInfo> list) {
        this.mContext = context;
        this.mStoriesList = list;
        mShareUtil = new SharedUtil(context);
    }

    public void update(List<StoriesInfo> list) {
        this.mStoriesList = list;
        notifyDataSetChanged();
    }

    public void add(List<StoriesInfo> list) {
        this.mStoriesList.addAll(list);
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public int getCount() {
        return mStoriesList == null ? 0 : mStoriesList.size();
    }

    @Override
    public StoriesInfo getItem(int i) {
        return mStoriesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.home_list_item_layout, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final StoriesInfo info = getItem(i);
        Boolean readId = mShareUtil.get(Boolean.class, info.id + "");
        if (!AppRunningContext.isNightMode()) {
            if (readId != null) {
                if (readId) {
                    viewHolder.titleTxt.setTextColor(mContext.getResources().getColor(R.color.bg_pressed));
                } else {
                    viewHolder.titleTxt.setTextColor(mContext.getResources().getColor(R.color.txt_main));
                }
            } else {
                viewHolder.titleTxt.setTextColor(mContext.getResources().getColor(R.color.txt_main));
            }
        } else {
            if (readId != null) {
                if (readId) {
                    viewHolder.titleTxt.setTextColor(mContext.getResources().getColor(R.color.night_mode_item_read));
                } else {
                    viewHolder.titleTxt.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            } else {
                viewHolder.titleTxt.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }
        viewHolder.titleTxt.setText(info.title);
        viewHolder.logoImg.setImageUrl(info.images.get(0));
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(info.id);
                }
            }
        });
        if (info.multipic) {
            viewHolder.multiPicTxt.setVisibility(View.VISIBLE);
        } else {
            viewHolder.multiPicTxt.setVisibility(View.GONE);
        }
        return view;
    }

    public class ViewHolder {
        @Bind(R.id.layout_item)
        ViewGroup itemLayout;
        @Bind(R.id.txt_title)
        TextView titleTxt;
        @Bind(R.id.txt_multipic)
        TextView multiPicTxt;
        @Bind(R.id.img_logo)
        CustomImageView logoImg;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface ItemClickListener {
        void onItemClick(int id);
    }
}
