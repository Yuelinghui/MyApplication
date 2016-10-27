package com.yuelinghui.personal.myapplication.core;

import android.app.Application;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;

/**
 * Created by yuelinghui on 16/9/30.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppRunningContext.init(this);

    }
}
