package com.yuelinghui.personal.widget.fragmentadapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * Created by yuelinghui on 16/10/13.
 */

public abstract class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {


    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 解决fragment恢复时导致其中控件找不到类异常（自定义控件的SaveState无法找到对应的classloader）
        // http://stackoverflow.com/questions/11381470/classnotfoundexception-when-unmarshalling-android-support-v4-view-viewpagersav
        final Object fragment = super.instantiateItem(container, position);
        try {
            final Field saveFragmentStateField = Fragment.class
                    .getDeclaredField("mSavedFragmentState");
            saveFragmentStateField.setAccessible(true);
            final Bundle savedFragmentState = (Bundle) saveFragmentStateField
                    .get(fragment);
            if (savedFragmentState != null) {
                savedFragmentState.setClassLoader(fragment.getClass()
                        .getClassLoader());
            }
        } catch (Exception e) {
        }
        return fragment;
    }
}
