package com.yuelinghui.personal.widget.bannerview;

import android.support.v4.app.FragmentManager;

import com.yuelinghui.personal.maframe.util.ListUtil;
import com.yuelinghui.personal.widget.core.ui.BaseFragment;
import com.yuelinghui.personal.widget.fragmentadapter.CustomBaseFragmentPagerAdapter;
import com.yuelinghui.personal.widget.fragmentadapter.CustomPagerFragment;
import com.yuelinghui.personal.widget.fragmentadapter.FragmentDescriptor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class InfiniteFragmentPagerAdapter<T extends Serializable> extends CustomBaseFragmentPagerAdapter<T> {


    public InfiniteFragmentPagerAdapter(FragmentManager fm,
                                        List<FragmentDescriptor<T>> fragmentDescriptors) {
        super(fm, fragmentDescriptors);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public BaseFragment getItem(int position) {
        if (null == fragmentDescriptors) {
            return null;
        }
        // 计算相对的position，然后再获取对应的描述器加载
        int relativePosition = position % fragmentDescriptors.size();
        FragmentDescriptor<T> descriptor = fragmentDescriptors
                .get(relativePosition);
        return CustomPagerFragment.generateFragment(descriptor);
    }

    @Override
    public int getCount() {
        int size = ListUtil.size(fragmentDescriptors);
        // 如果需要加载的页面数大于1，则开启无限滑动
        // 如果只有一页，则只加载一页
        if (size > 1) {
            return Integer.MAX_VALUE;
        } else {
            return size;
        }
    }



}
