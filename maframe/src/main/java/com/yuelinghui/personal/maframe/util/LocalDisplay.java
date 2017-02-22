package com.yuelinghui.personal.maframe.util;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class LocalDisplay {

    public static int dp2px(float dp) {
        final float scale = RunningEnvironment.sScreenDensity;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        return (int) (pxValue / RunningEnvironment.sScreenDensity + 0.5f);
    }

    public static int px2sp(float pxValue) {
        return (int) (pxValue / RunningEnvironment.sFontScale + 0.5f);
    }

    public static int sp2px(float spValue) {
        return (int) (spValue * RunningEnvironment.sFontScale + 0.5f);
    }

    public static int sp2dp(float spValue) {
        float pxValue = spValue * RunningEnvironment.sFontScale + 0.5f;
        return (int) (pxValue / RunningEnvironment.sScreenDensity + 0.5f);
    }

}
