package com.yuelinghui.personal.widget.fragmentadapter;

import android.os.Bundle;

import com.yuelinghui.personal.widget.core.ui.BaseFragment;

/**
 * Created by yuelinghui on 16/10/13.
 */

public abstract class CustomPagerFragment extends BaseFragment {


    /**
     * 外部参数key
     */
    protected static final String ARG_INFO = "argInfo";

    protected static final String ARG_POSITION = "argPosition";

    /**
     * 通过Fragment描述器动态实例化Fragment，并且设置相应的Bundle参数
     *
     * @param descriptor
     * @return
     */
    public static BaseFragment generateFragment(FragmentDescriptor<?> descriptor) {
        if (null != descriptor && descriptor.mFragmentClazz != null) {
            try {
                CustomPagerFragment fragment = (CustomPagerFragment) descriptor.mFragmentClazz
                        .newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ARG_INFO, descriptor.mInfo);
                bundle.putInt(ARG_POSITION, descriptor.mPosition);
                fragment.setArguments(bundle);
                fragment.customBundle(bundle, descriptor);
                return fragment;
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 每一个Fragment重写该方法，根据自己的需要将descriptor中的对应参数设置到bundle中
     *
     * @param bundle
     * @param descriptor
     */
    protected void customBundle(Bundle bundle, FragmentDescriptor<?> descriptor) {
    }
}
