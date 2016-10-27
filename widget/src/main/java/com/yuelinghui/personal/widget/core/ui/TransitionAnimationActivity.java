package com.yuelinghui.personal.widget.core.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.yuelinghui.personal.widget.R;


/**
 * Created by yuelinghui on 16/10/11.
 */

public class TransitionAnimationActivity extends FragmentActivity {


    private final static String SAVE_THEME = "saveTheme";

    private int mTheme = 0;

    @Override
    public void setTheme(int resid) {
        super.setTheme(resid);
        mTheme = resid;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_THEME, mTheme);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mTheme = savedInstanceState.getInt(SAVE_THEME);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
