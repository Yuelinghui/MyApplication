package com.yuelinghui.personal.widget.core.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuelinghui.personal.widget.R;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class ErrorFragment extends BaseFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.error_fragment_layout, container,
                false);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mActivity != null) {
                    mActivity.load();
                }
            }
        });

        return view;
    }
}
