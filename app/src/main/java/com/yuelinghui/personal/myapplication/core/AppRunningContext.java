package com.yuelinghui.personal.myapplication.core;

import android.app.Application;

/**
 * Created by yuelinghui on 16/9/30.
 */

public class AppRunningContext extends RunningContext {

    public static final String URL_BASE = "http://news-at.zhihu.com/api/4/news";

    private static boolean sIsNightMode = false;
    private static byte[] sNightModeLock = new byte[0];

    public static void init(Application app) {
        RunningContext.init(app);
        setNightMode(AppConfig.isNightMode());
    }

    public static void setNightMode(boolean isNight) {
        synchronized (sNightModeLock) {
            if (sIsNightMode == isNight) {
                return;
            }
            sIsNightMode = isNight;
            AppConfig.setNightMode(isNight);
        }
    }

    public static boolean isNightMode() {
        synchronized (sNightModeLock) {
            return sIsNightMode;
        }
    }
}
