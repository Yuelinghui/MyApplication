package com.yuelinghui.personal.widget.titlebar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.yuelinghui.personal.widget.R;
import com.yuelinghui.personal.widget.core.RunningContext;

import java.util.List;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class CustomActionMenu {


    private Context mContext = null;
    /**
     * 弹出窗口
     */
    private PopupWindow mPopupWindow;
    /**
     * 显示菜单listview
     */
    private ListView listView;
    /**
     * 菜单列表
     */
    private List<CustomAction> mMenuList = null;
    /**
     * 选中监听
     */
    private CustomTitleBar.ActionClickListener mActionClickListener = null;

    public CustomActionMenu(Context context) {
        mContext = context;
        initView();
    }

    private void initView() {

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.custom_title_popmenu, null);
        view.setOnClickListener(mMenuClick);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(mMenuItemClick);

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (RunningContext.sScreenWidth != 0) {
            mPopupWindow.setWidth((int) (RunningContext.sScreenWidth * 0.45));
        }
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.popuWindowAnimation);
//        mPopupWindow.setBackgroundDrawable(new ColorDrawable(-00000));

    }

    /**
     * 是否正在显示
     *
     * @return
     */
    public boolean isShow() {
        return mPopupWindow.isShowing();
    }

    public void setMenuList(List<CustomAction> menuList) {
        this.mMenuList = menuList;
        if (listView != null) {
            listView.setAdapter(new CustomActionAdapter(mContext, menuList));
        }
    }

    /**
     * 设置菜单选中监听
     *
     * @param listener
     */
    public void setActionClickListener(CustomTitleBar.ActionClickListener listener) {
        this.mActionClickListener = listener;
    }

    /**
     * 点击空白收回菜单
     */
    private View.OnClickListener mMenuClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    /**
     * 列表点击，选中菜单
     */
    private AdapterView.OnItemClickListener mMenuItemClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            try {
                if (mActionClickListener != null && mMenuList != null) {
                    mActionClickListener.onClick(mMenuList.get(position));
                }
            } catch (Exception e) {
            }
            dismiss();
        }
    };

    /**
     * 展示菜单
     *
     * @param view
     */
    public void show(View view) {
        if (view != null && mPopupWindow != null && !mPopupWindow.isShowing()) {
            // mPopupWindow.showAsDropDown(view);
            mPopupWindow.showAsDropDown(view);

        }
    }

    /**
     * 收起菜单
     */
    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }
}
