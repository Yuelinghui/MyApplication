package com.yuelinghui.personal.myapplication.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.yuelinghui.personal.maframe.UIData;
import com.yuelinghui.personal.myapplication.R;
import com.yuelinghui.personal.myapplication.home.ui.MainActivity;
import com.yuelinghui.personal.widget.core.ui.BaseActivity;

/**
 * Created by yuelinghui on 16/10/14.
 */

public class SplashActivity extends BaseActivity {

    private static final long DELAY_TIME = 2 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startHome();
            }
        }, DELAY_TIME);
    }

    private void startHome() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected UIData initUIData() {
        return null;
    }
}
