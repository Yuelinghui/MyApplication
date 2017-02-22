package com.yuelinghui.personal.widget.core.ui;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.yuelinghui.personal.maframe.UIData;
import com.yuelinghui.personal.maframe.concurrent.CancelListener;

import java.lang.reflect.Type;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class BaseFragment extends Fragment {

    /**
     * 缓存数据
     */
    protected UIData mUIData = null;

    /**
     * 父activity
     */
    protected BaseActivity mActivity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = (BaseActivity) getActivity();
        this.mUIData = mActivity.mUIData;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (BaseActivity) activity;
        this.mUIData = mActivity.mUIData;
    }

    /**
     * 初始化模块标题
     *
     * @author wyqiuchunlong
     * @return 模块标题
     */
    protected String initTitle() {
        return null;
    }

    /**
     * fragmert恢复时 设置标题
     */
    @Override
    public void onResume() {
        super.onResume();
        // 设置标题
        String title = initTitle();
        if (!TextUtils.isEmpty(title)) {
            mActivity.setSimpleTitle(title);
        }
    }

    /**
     * 模拟物理返回键
     */
    protected void onBack() {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {
                }
            }
        }.start();

    }

    /**
     *
     */
    @Override
    public void startActivity(Intent intent) {
        mActivity.startActivity(intent);
    }

    /**
     * 显示进度
     *
     * @author wyqiuchunlong
     */
    protected void showProgress(String message) {
        mActivity.showProgress(message);
    }

    /**
     * 显示进度
     *
     * @author wyqiuchunlong
     */
    protected void showProgress(String message,
                                final CancelListener cancelHandler) {
        mActivity.showProgress(message, cancelHandler);
    }

    /**
     * 显示进度
     *
     * @author wyqiuchunlong
     */
    protected void showProgress(String message,
                                final CancelListener cancelHandler, final int cancelType) {
        mActivity.showProgress(message, cancelHandler, cancelType);
    }

    /**
     * 显示进度
     *
     * @author wyqiuchunlong
     */
    protected boolean showNetProgress(String message) {
        return mActivity.showNetProgress(message);
    }

    /**
     * 显示进度
     *
     * @author wyqiuchunlong
     */
    protected boolean showNetProgress(String message,
                                      final CancelListener cancelHandler) {
        return mActivity.showNetProgress(message, cancelHandler);
    }

    /**
     * 显示进度
     *
     * @author wyqiuchunlong
     */
    protected boolean showNetProgress(String message,
                                      final CancelListener cancelHandler, final int cancelType) {
        return mActivity.showNetProgress(message, cancelHandler, cancelType);
    }

    /**
     * 隐藏进度条
     *
     * @author wyqiuchunlong
     */
    protected void dismissProgress() {
        mActivity.dismissProgress();
    }

    /**
     * 获取列表缓存数据
     *
     * @param key
     * @param type
     * @return
     */
    protected Object getCacheList(String key, Type type) {
        return mActivity.getCacheList(key, type);
    }

    /**
     * 获取对象缓存数据
     *
     * @param key
     * @return
     */
    protected Object getCacheData(String key, Class<?> clazz) {
        return mActivity.getCacheData(key, clazz);
    }

    /**
     * 设置緩存
     *
     * @param data
     */
    protected void setCacheData(String key, Object data) {
        mActivity.setCacheData(key, data);
    }

    /**
     * 判断对象和缓存数据是否存在
     *
     * @param key
     * @param data
     * @return
     */
    protected boolean containCacheData(String key, Object data) {
        return mActivity.containCacheData(key, data);
    }

    protected boolean onBackPressed() {
        return false;
    }
}
