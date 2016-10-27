package com.yuelinghui.personal.myapplication.core;

import com.yuelinghui.personal.myapplication.util.SharedUtil;
import com.yuelinghui.personal.widget.core.RunningContext;

/**
 * Created by yuelinghui on 16/10/17.
 */

public class AppConfig {

    private static final String NIGHT_MODE = "NIGHT_MODE";

    private static SharedUtil sSharedUtil = new SharedUtil(RunningContext.sAppContext);

    public static void setNightMode(boolean isNight) {
        sSharedUtil.set(isNight, NIGHT_MODE);
    }

    public static boolean isNightMode() {
        Boolean isNight = sSharedUtil.get(Boolean.class, NIGHT_MODE);
        if (isNight != null) {
            return isNight;
        }
        return false;
    }
}
