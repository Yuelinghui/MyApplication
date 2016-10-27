package com.yuelinghui.personal.widget.titlebar;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuelinghui.personal.widget.R;
import com.yuelinghui.personal.widget.image.CustomImageView;

import java.util.List;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class CustomActionAdapter extends BaseAdapter {

    private Context mContext = null;
    /**
     * 缓存菜单数据
     */
    private List<CustomAction> mMenuList = null;

    /**
     * 构造时传入数据
     *
     * @param context
     * @param menuList
     */
    public CustomActionAdapter(Context context, List<CustomAction> menuList) {
        this.mContext = context;
        this.mMenuList = menuList;
    }

    @Override
    public int getCount() {
        return mMenuList == null ? 0 : mMenuList.size();
    }

    @Override
    public CustomAction getItem(int position) {
        return mMenuList == null ? null : mMenuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.custom_title_pop_menu_item, parent, false);
            holder.mTxt = (TextView) convertView.findViewById(R.id.txt_action);
            holder.mImg = (CustomImageView) convertView
                    .findViewById(R.id.img_action);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CustomAction menu = mMenuList.get(position);
        if (menu != null) {
            if (!TextUtils.isEmpty(menu.menuImage) || menu.imgResId != 0) {
                holder.mImg.setVisibility(View.VISIBLE);
                holder.mImg.setImageUrl(menu.menuImage, menu.imgResId);

                holder.mTxt.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else {
                holder.mImg.setVisibility(View.GONE);
                holder.mImg.setImageBitmap(null);

                holder.mTxt.setGravity(Gravity.CENTER);
            }
            holder.mTxt.setText(menu.menuTitle);
        }

        return convertView;
    }

    class ViewHolder {
        private TextView mTxt;
        private CustomImageView mImg;
    }
}
