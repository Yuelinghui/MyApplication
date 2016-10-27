package com.yuelinghui.personal.widget.core.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yuelinghui.personal.maframe.UIData;
import com.yuelinghui.personal.maframe.concurrent.CancelListener;
import com.yuelinghui.personal.maframe.result.ResultHandler;
import com.yuelinghui.personal.network.NetModel;
import com.yuelinghui.personal.widget.CustomButton;
import com.yuelinghui.personal.widget.CustomProgressDialog;
import com.yuelinghui.personal.widget.R;
import com.yuelinghui.personal.widget.core.BroadcastAction;
import com.yuelinghui.personal.widget.core.RunningContext;
import com.yuelinghui.personal.widget.dialog.CustomDialog;
import com.yuelinghui.personal.widget.image.CustomImageView;
import com.yuelinghui.personal.widget.titlebar.CustomAction;
import com.yuelinghui.personal.widget.titlebar.CustomTitleBar;
import com.yuelinghui.personal.widget.toast.CustomToast;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by yuelinghui on 16/10/11.
 */

public abstract class BaseActivity extends TransitionAnimationActivity {

    /**
     * 页面数据key
     */
    public static final String UIDATA = "uidata";

    // /**
    // * 存储startActivityforResult处理的view的容器的键
    // */
    // public static final String RESULT_VIEW = "resultView";

    /**
     * 业务数据
     */
    public UIData mUIData = null;

    /**
     * 进度条 *
     */
    private CustomProgressDialog mProgressDialog = null;

    /**
     * 用于隐藏键盘
     */
    private InputMethodManager imm = null;

    /**
     * 通用 activity 布局（无滚动，需Fragment自己处理滚动）
     */
    final static public int FRAGMENT_LAYOUT = R.layout.base_activity_layout;

    private static IntentFilter mIntentFilter = new IntentFilter(
            BroadcastAction.EXIT_APP);

    private ExitAPPReciver mExitReciver = null;

    /**
     * 标题
     */
    public TextView mTitleTxt = null;
    /**
     * 标题---右侧按钮
     */
    public CustomButton mTitleRightBtn = null;
    /**
     * 标题---右侧按钮图标
     */
    public CustomImageView mTitleRightImg = null;
    /**
     * 标题---左侧按钮图标
     */
    public CustomImageView mTitleLeftImg = null;
    /**
     * 标题---父布局
     */
    public View mTitleLayout = null;
    /**
     * 自定义标题
     */
    public FrameLayout mTitleCustomLayout = null;
    public ViewGroup mTilteBaseLayout = null;
    // /**
    // * 分割线
    // */
    // private View mTitleDivider = null;
    /**
     * 标题栏自定义View
     */
    private CustomTitleBar mTitleBar = null;
    /**
     * fragment 容器
     */
    private ScrollView mScrollView = null;

    // /**
    // * 需要通过onActivityForResult获取返回值的view
    // */
    // private HashSet<ViewReceiver> mResultViewSet = null;

    /**
     * 解决4.0以后用户调整系统字号问题，使用此配置，APP字体大小不随系统字号大小变动
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        /*
         * 优化字体设置 20150825 判断字体改变后，才需要更新默认字体
		 */
        Configuration config = res.getConfiguration();
        if (1 != config.fontScale) {
            // 恢复默认字体
            config.fontScale = 1f;
            res.updateConfiguration(config, null);
        }
        return res;
    }

    /**
     * 设置title右侧菜单
     *
     * @param menuList
     */
    public void setActions(List<CustomAction> menuList) {
        if (mTitleBar != null) {
            mTitleBar.setActions(menuList);
        }
    }

    public void setActionClickListener(CustomTitleBar.ActionClickListener listener) {
        if (mTitleBar != null) {
            mTitleBar.setActionClickListener(listener);
        }
    }

    /**
     * 设置titleBar是否可见
     *
     * @param visible
     */
    public void setTitleBarVisible(boolean visible) {
        if (mTitleBar != null) {
            mTitleBar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置复合标题
     *
     * @param title         中间title
     * @param rightTxt      右侧文本，为空则不显示
     * @param rightDarwable 右侧imageview，为空则不显示
     * @param clickable     是否可点击，弹出下拉菜单
     */
    public void setComplexTilte(String title, String rightTxt,
                                Drawable rightDarwable, boolean clickable) {
        if (mTitleBar != null) {
            mTitleBar
                    .setComplexTilte(title, rightTxt, rightDarwable, clickable);
        }
    }

    /**
     * 设置标题
     */
    public void setSimpleTitle(String title) {
        if (mTitleBar != null) {
            mTitleBar.setSimpleTitle(title);
        }
    }

    /**
     * 设置标题
     */
    public void setSimpleTitle(String title, int titleTxtColor) {
        if (mTitleBar != null) {
            mTitleBar.setSimpleTitle(title, titleTxtColor);
        }
    }

    /**
     * 设置分割线是否可见（默认不可见）
     *
     * @param enable
     */
    public void setTitleDividerVisiable(boolean enable) {
        if (enable && mTitleBar != null) {
            mTitleBar.setTitleDividerVisiable(enable);
        }
    }

    /**
     * 设置返回键点击监听
     *
     * @param listener
     * @author wyqiuchunlong
     */
    public void setBackClickListener(View.OnClickListener listener) {
        if (mTitleLeftImg != null) {
            mTitleLeftImg.setOnClickListener(listener);
        }
    }

    /**
     * 默认点击监听
     */
    private View.OnClickListener mBackDefaultClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    /**
     * 设置自定义title
     *
     * @param customView
     */
    public void setCustomTitle(View customView) {
        if (mTitleBar != null) {
            mTitleBar.setCustomTitle(customView);
        }
    }

    /**
     * 设置标题和布局文件
     *
     * @param layoutResID
     * @param title
     */
    protected void setContentViewAndTitle(int layoutResID, CharSequence title) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.setContentView(layoutResID);

        bindToTitleBar(title, 0);
    }

    /**
     * 设置标题和布局view
     *
     * @param view  布局view
     * @param title
     */
    protected void setContentViewAndTitle(View view, ViewGroup.LayoutParams params,
                                          CharSequence title, int titleBarColor) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.setContentView(view, params);

        bindToTitleBar(title, titleBarColor);
    }

    /**
     * 设置标题和布局文件
     *
     * @param layoutResID
     * @param title
     */
    public void setContentViewAndTitle(int layoutResID, CharSequence title,
                                       int titleBarColor) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.setContentView(layoutResID);

        bindToTitleBar(title, titleBarColor);
    }

    /**
     * 设置titlebar背景颜色
     *
     * @param titleBarColor
     */
    public void setTitleBarColor(int titleBarColor) {
        if (mTitleBar != null) {
            mTitleBar.setTitleBarColor(titleBarColor);
        }
    }

    /**
     * 绑定titleBar组件与CPActivity中原有的title访问变量 / 小米2手机title变色问题
     * 设置titleBarColor为0，白色
     */
    private void bindToTitleBar(CharSequence title, int titleBarColor) {

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.base_title_bar);
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mTitleLayout = mTitleBar.getTitleLayout();
        mTitleTxt = mTitleBar.getTitleTxt();
        mTitleRightBtn = mTitleBar.getTitleRightBtn();
        mTitleRightImg = mTitleBar.getTitleRightImg();
        mTilteBaseLayout = mTitleBar.getTilteBaseLayout();
        mTitleCustomLayout = mTitleBar.getTitleCustomLayout();
        mTitleLeftImg = mTitleBar.getTitleLeftImg();
        mTitleLeftImg.setOnClickListener(mBackDefaultClick);

        if (!TextUtils.isEmpty(title)) {
            mTitleBar.setSimpleTitle(title.toString());
        }

        mTitleBar.setTitleBarColor(titleBarColor);
    }

    /**
     * 设置标题栏左侧图片
     *
     * @param width
     * @param height
     */
    public void setLeftImageSize(int width, int height) {
        setImageSize(width, height, mTitleLeftImg);
    }

    /**
     * 设置标题栏右侧图片
     *
     * @param width
     * @param height
     */
    public void setRightImageSize(int width, int height) {
        setImageSize(width, height, mTitleRightImg);
    }

    /**
     * 设置图片大小
     *
     * @param width
     * @param height
     */
    private void setImageSize(int width, int height, CustomImageView image) {
        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.width = width
                + getResources().getDimensionPixelSize(R.dimen.margin_h_middle)
                * 2;
        params.height = height
                + getResources().getDimensionPixelSize(R.dimen.margin_h_middle)
                * 2;
        image.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initScreenParm();
        // 加载数据
        if (savedInstanceState == null) {
            mUIData = initUIData();
        } else {
            savedInstanceState.setClassLoader(getClass().getClassLoader());
            mUIData = (UIData) savedInstanceState.getSerializable(UIDATA);
        }
        super.onCreate(savedInstanceState);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // 注册 APP_EXIT 广播
        mExitReciver = new ExitAPPReciver();
        registerReceiver(mExitReciver, mIntentFilter);
    }

    @Override
    protected void onDestroy() {

        // 停止网络请求
        NetModel.cancel(this);

        super.onDestroy();

        // 解除注册的 APP_EXIT 广播
        if (mExitReciver != null) {
            unregisterReceiver(mExitReciver);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (mScrollView == null) {
            View view = findViewById(R.id.fragment_container);
            if (view instanceof ScrollView) {
                mScrollView = (ScrollView) view;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 启动Activity
     */
    @Override
    public void startActivity(Intent intent) {
        start(intent, -1);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        start(intent, requestCode);
    }

    private void start(Intent intent, int requestCode) {
        // 隐藏键盘
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        List<android.support.v4.app.Fragment> list = getSupportFragmentManager().getFragments();
        BaseFragment fragment = null;
        if (list != null && list.size() > 0) {
            if (list.size() > count) {
                // 处理startFragment和startFirstFragment混合模式
                for (int i = list.size() - 1; i >= 0; i--) {
                    if (list.get(i) != null) {
                        fragment = (BaseFragment) list.get(i);
                        if (!fragment.isAdded() || !fragment.isVisible()) {
                            continue;
                        }
                        break;
                    }
                }
            } else {
                // 处理startFragment模式
                if (count > 0 && list.get(count - 1) != null
                        && list.get(count - 1) instanceof BaseFragment) {
                    fragment = (BaseFragment) list.get(count - 1);
                }
            }
        }
        if (fragment == null || !fragment.isAdded() || !fragment.isVisible()) {
            super.onBackPressed();
            return;
        }
        if (!fragment.onBackPressed()) {
            if (count <= 0) {
                super.onBackPressed();
                return;
            }
            getSupportFragmentManager().popBackStack();
            return;
        }
    }

    public void finish() {
        // 确认进度条消失
        dismissProgress();
        super.finish();
    }

    /**
     * 切换fragment
     *
     * @param fragment
     */
    public void startFirstFragment(android.support.v4.app.Fragment fragment) {

        if (isFinishing()) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.fragment_container, fragment);
        ft.commitAllowingStateLoss();
    }

    /**
     * 启动做滑动动画fragment
     *
     * @param fragment
     * @author wyqiuchunlong
     */
    public void startFragment(Fragment fragment) {

        if (isFinishing()) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out,
                R.anim.push_left_in, R.anim.push_right_out);
        ft.replace(R.id.fragment_container, fragment, fragment.getClass()
                .getName());
        ft.addToBackStack(fragment.getClass().getName());
        ft.commitAllowingStateLoss();
    }

    /**
     * 启动Fragment，不入栈
     *
     * @param fragment
     * @author wyqiuchunlong
     */
    public void startFragmentWithoutHistory(Fragment fragment) {

        if (isFinishing()) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out,
                R.anim.push_left_in, R.anim.push_right_out);
        ft.replace(R.id.fragment_container, fragment);
        // ft.addToBackStack(fragment.getClass().getName());
        ft.commitAllowingStateLoss();
    }

    /**
     * 切换fragment
     *
     * @param fragment
     */
    public void startFragmentWithoutAnim(Fragment fragment) {

        if (isFinishing()) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commitAllowingStateLoss();
    }

    /**
     * 退回到上一个fragment
     *
     * @author wyqiuchunlong
     */
    public void backToFragment() {
        try {
            getSupportFragmentManager().popBackStack();
        } catch (Exception e) {
        }
    }

    public void backToFragmentImmediate() {
        try {
            getSupportFragmentManager().popBackStackImmediate();
        } catch (Exception e) {
        }
    }

    /**
     * 退回到指定的fragment之前的都清空
     *
     * @param clazz
     * @author wyqiuchunlong
     */
    public void backToFragment(Class<? extends android.support.v4.app.Fragment> clazz) {
        String fragmentName = clazz.getName();
        FragmentManager manager = getSupportFragmentManager();
        try {
            boolean pop = true;
            while (pop) {
                pop = manager.popBackStackImmediate();
            }

            android.support.v4.app.Fragment newFragment = null;
            List<android.support.v4.app.Fragment> fs = manager.getFragments();
            if (fs != null && fs.size() > 0) {
                for (android.support.v4.app.Fragment f : fs) {
                    if (f != null
                            && f.getClass().getName().compareTo(fragmentName) == 0) {
                        newFragment = f;
                        break;
                    }
                }
            }
            if (newFragment == null) {
                newFragment = clazz.newInstance();
            }

            FragmentTransaction ft = manager.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(R.id.fragment_container, newFragment);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
        }
    }

    /**
     * 退回到指定的fragment，之前的不清空
     *
     * @param clazz
     * @author wyqiuchunlong
     */
    public void backToStackFragment(Class<? extends android.support.v4.app.Fragment> clazz) {
        String fragmentName = clazz.getName();
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentByTag(fragmentName) != null) {
            manager.popBackStack(fragmentName, 0);
        } else {
            try {
                boolean pop = true;
                while (pop) {
                    pop = manager.popBackStackImmediate();
                }
                android.support.v4.app.Fragment newFragment = null;
                List<android.support.v4.app.Fragment> fs = manager.getFragments();
                if (fs != null && fs.size() > 0) {
                    for (android.support.v4.app.Fragment f : fs) {
                        if (f != null
                                && f.getClass().getName()
                                .compareTo(fragmentName) == 0) {
                            newFragment = f;
                            break;
                        }
                    }
                }
                if (newFragment == null) {
                    newFragment = clazz.newInstance();
                }
                FragmentTransaction ft = manager.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(R.id.fragment_container, newFragment);
                ft.commitAllowingStateLoss();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 判断当前显示的是不是此Fragment
     *
     * @return
     * @author wyqiuchunlong
     */
    protected boolean isCurrentFragment(Fragment fragment) {
        return (fragment != null && getLastFragmentName().equals(
                fragment.getClass().getName()));

    }

    /**
     * 判断当前显示的是不是此Fragment
     *
     * @param fragmentName
     * @return
     */
    protected boolean isCurrentFragment(String fragmentName) {
        if (TextUtils.isEmpty(fragmentName)) {
            return false;
        }
        return getLastFragmentName().equals(fragmentName);
    }

    /**
     * 获取栈中最后一个Fragment
     *
     * @return
     */
    protected String getLastFragmentName() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            return "";
        }
        FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(
                count - 1);
        return entry.getName();
    }

    /**
     * 显示进度条
     *
     * @author wyqiuchunlong
     */
    protected void showProgress(String message) {
        showProgress(message, false, null, CancelListener.CANCEL_NONE);
    }

    /**
     * 显示进度条
     *
     * @author wyqiuchunlong
     */
    protected void showProgress(String message,
                                final CancelListener cancelHandler) {
        showProgress(message, false, cancelHandler, CancelListener.CANCEL_EMPTY);
    }

    /**
     * 显示进度条
     *
     * @author wyqiuchunlong
     */
    protected void showProgress(String message,
                                final CancelListener cancelHandler, final int cancelType) {
        showProgress(message, false, cancelHandler, cancelType);
    }

    /**
     * 显示进度条
     *
     * @author wyqiuchunlong
     */
    protected void showProgress(String message, final int cancelType) {
        showProgress(message, false, null, cancelType);
    }

    /**
     * 显示进度条
     *
     * @author wyqiuchunlong
     */
    public boolean showNetProgress(String message) {
        return showProgress(message, true, null, CancelListener.CANCEL_NONE);
    }

    /**
     * 显示进度条
     *
     * @author wyqiuchunlong
     */
    public boolean showNetProgress(String message,
                                   final CancelListener cancelHandler) {
        return showProgress(message, true, cancelHandler,
                CancelListener.CANCEL_EMPTY);
    }

    /**
     * 显示进度条
     *
     * @author wyqiuchunlong
     */
    public boolean showNetProgress(String message,
                                   final CancelListener cancelHandler, final int cancelType) {
        return showProgress(message, true, cancelHandler, cancelType);
    }

    /**
     * 显示进度条
     *
     * @author wyqiuchunlong
     */
    public boolean showProgress(String message, boolean checkNet,
                                final CancelListener cancelHandler, final int cancelType) {

        if (checkNet) {
            boolean isConnected = RunningContext.checkNetWork();
            if (!isConnected) {
                CustomToast.makeText(getString(R.string.error_net_unconnect))
                        .show();
                return isConnected;
            }
        }
        if (mProgressDialog == null) {
            // 初始化全局进度条
            mProgressDialog = new CustomProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.setCancelable(cancelHandler != null
                || cancelType != CancelListener.CANCEL_NONE);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                ResultHandler.cancel(BaseActivity.this);

                if (cancelHandler != null) {
                    cancelHandler.onCancel(cancelType);
                }

                switch (cancelType) {
                    case CancelListener.CANCEL_ERROR:
                        startFirstFragment(new ErrorFragment());
                        break;
                    case CancelListener.CANCEL_FINISH:
                        finish();
                        break;
                    case CancelListener.CANCEL_EMPTY:
                    case CancelListener.CANCEL_NONE:
                    default:
                        break;
                }
            }

        });
        mProgressDialog
                .setMessage(TextUtils.isEmpty(message) ? getString(R.string.common_loading)
                        : message);
        mProgressDialog.show();

        return true;
    }

    /**
     * 隐藏进度条
     *
     * @author wyqiuchunlong
     */
    public void dismissProgress() {

        if (!isFinishing() && mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 监测网络
     *
     * @return
     */
    public boolean checkNetWork() {
        if (!RunningContext.checkNetWork()) {
            CustomToast.makeText(getString(R.string.error_net_unconnect)).show();
            return false;
        }
        return true;
    }

    /**
     * 弹出是否终端当前Activity操作Dialog
     *
     * @author wyqiuchunlong
     */
    protected void cancelActivity() {
        new CustomDialog(this).setMsg(getString(R.string.common_alert_message))
                .setOkButton(null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseActivity.this.finish();
                    }
                }).setCancelButton(null, null).show();
    }

    /**
     * 截获第一个业务页面和最后一个业务页面
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出应用广播监听
     *
     * @author xingtongju
     */
    class ExitAPPReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(BroadcastAction.EXIT_APP)) {
                BaseActivity.this.finish();
            }
        }

    }

    /**
     * 缓存数据
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.setClassLoader(getClass().getClassLoader());
        super.onSaveInstanceState(outState);
        outState.putSerializable(UIDATA, mUIData);
    }

    /**
     * 还原数据
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        savedInstanceState.setClassLoader(getClass().getClassLoader());
        super.onRestoreInstanceState(savedInstanceState);
        mUIData = (UIData) savedInstanceState.getSerializable(UIDATA);
    }

    public boolean needLogin() {
        return true;
    }

    public boolean needRealName() {
        return false;
    }

    public boolean supportForeignRealName() {
        return true;
    }

    public boolean needNetwork() {
        return false;
    }

    public boolean needGesture() {
        return true;
    }

    /**
     * 初始化页面数据 子类实现
     *
     * @return
     * @author wyqiuchunlong
     */
    abstract protected UIData initUIData();

    /**
     * 刷新数据
     */
    protected void load() {

    }

    ;

    /**
     * 设置緩存
     *
     * @param data
     */
    protected void setCacheData(String key, Object data) {
        SharedPreferences mSharedPreferences = this.getSharedPreferences(this
                .getClass().getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String json = new Gson().toJson(data);
        editor.putString(key, json);
        editor.commit();
    }

    /**
     * 获取对象缓存数据
     *
     * @param key
     * @return
     */
    protected Object getCacheData(String key, Class<?> clazz) {
        SharedPreferences mSharedPreferences = this.getSharedPreferences(this
                .getClass().getName(), Context.MODE_PRIVATE);
        String jsonData = "";
        jsonData = mSharedPreferences.getString(key, "");
        Object obj = new Gson().fromJson(jsonData, clazz);
        return obj;
    }

    /**
     * 获取列表缓存数据
     *
     * @param key
     * @param type
     * @return
     */
    protected Object getCacheList(String key, Type type) {
        SharedPreferences mSharedPreferences = this.getSharedPreferences(this
                .getClass().getName(), Context.MODE_PRIVATE);
        String jsonData = "";
        jsonData = mSharedPreferences.getString(key, "");
        Object obj = new Gson().fromJson(jsonData, type);
        return obj;
    }

    /**
     * 判断对象和缓存数据是否存在
     *
     * @param key
     * @param data
     * @return
     */
    protected boolean containCacheData(String key, Object data) {
        SharedPreferences mSharedPreferences = this.getSharedPreferences(this
                .getClass().getName(), Context.MODE_PRIVATE);
        String oldJsonData = mSharedPreferences.getString(key, "");
        String newJsonData = new Gson().toJson(data);
        return oldJsonData.equals(newJsonData);
    }

    /**
     * 设置滑动的 ScrollView
     *
     * @param scrollView
     */
    public void setScrollView(ScrollView scrollView) {
        mScrollView = scrollView;
    }

    /**
     * 滚动到某一view 下面
     */
    public void scrollToView(final View view) {
        scrollToView(view, 700);
    }

    /**
     * 滚动到某一view 下面
     */
    public void scrollToView(final View view, int delay) {
        if (view != null && mScrollView != null) {
            mScrollView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    int scrollBottom = mScrollView.getHeight() - 10;
                    int viewBottom = view.getBottom();
                    int a[] = {-1, -1};
                    view.getLocationOnScreen(a);
                    if (a[1] > scrollBottom) {
                        mScrollView.scrollTo(0, viewBottom - scrollBottom);
                    }
                }
            }, delay);
        }
    }

    /**
     * 将某一view滑动到scrollView的顶部
     *
     * @param view
     */
    public void scrollToTop(final View view) {
        if (view != null && mScrollView != null) {
            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    int scrollTop = 0;
                    int viewTop = view.getTop();
                    mScrollView.smoothScrollTo(0, viewTop - scrollTop);
                }
            }, 400);
        }
    }

    /**
     * 初始化手机屏幕参数
     */
    public void initScreenParm() {
        if (RunningContext.sScreenHeight == 0
                || RunningContext.sScreenWidth == 0
                || RunningContext.sScreenDpi == 0) {

            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels; // 屏幕宽度（像素）
            int height = metric.heightPixels; // 屏幕高度（像素）
            int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）

            RunningContext.sScreenHeight = height;
            RunningContext.sScreenWidth = width;
            RunningContext.sScreenDpi = densityDpi;
        }
    }

    public void hideSoftKeyboard() {
        View view = getWindow().peekDecorView();
        InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
