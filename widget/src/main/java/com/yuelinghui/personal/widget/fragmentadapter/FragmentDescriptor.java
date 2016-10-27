package com.yuelinghui.personal.widget.fragmentadapter;

import java.io.Serializable;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class FragmentDescriptor<T extends Serializable> {


    /**
     * 需要加载的Fragment的class
     */
    public Class<? extends CustomPagerFragment> mFragmentClazz;

    /**
     * fragment需要的信息的配置信息
     */
    public T mInfo;

    /**
     * 当前banner的相对position
     */
    public int mPosition;

    public FragmentDescriptor(Class<? extends CustomPagerFragment> fragmentClazz) {
        mFragmentClazz = fragmentClazz;
    }

    public FragmentDescriptor(Class<? extends CustomPagerFragment> fragmentClazz,
                              T info, int position) {
        this(fragmentClazz);
        mInfo = info;
        mPosition = position;
    }
}
