package com.yuelinghui.personal.widget.fragmentadapter;

import android.support.v4.app.FragmentManager;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuelinghui on 16/10/13.
 */

public abstract class CustomBaseFragmentPagerAdapter<T extends Serializable> extends CustomFragmentPagerAdapter {

    /**
     * Fragment的描述器
     */
    protected List<FragmentDescriptor<T>> fragmentDescriptors;

    public CustomBaseFragmentPagerAdapter(FragmentManager fm,
                                          List<FragmentDescriptor<T>> fragmentDescriptors) {
        super(fm);
        this.fragmentDescriptors = fragmentDescriptors;
    }

    /**
     * 更新descriptors
     *
     * @param fragmentDescriptors
     */
    public void updateDescriptors(List<FragmentDescriptor<T>> fragmentDescriptors) {
        this.fragmentDescriptors = fragmentDescriptors;
    }


}
