package com.yuelinghui.personal.myapplication.core;

import android.content.Intent;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;
import com.yuelinghui.personal.widget.core.AppData;
import com.yuelinghui.personal.widget.core.BroadcastAction;
import com.yuelinghui.personal.widget.toast.Toast;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class RunningContext extends RunningEnvironment{
    /**
     * 屏幕高幕
     */
    public static int sScreenHeight;
    /**
     * 屏幕宽
     */
    public static int sScreenWidth;
    /**
     * 屏幕密度dpi
     */
    public static int sScreenDpi;

    /**
     * APP配置数据
     */
    public static AppData sAppData = new AppData();


    /**
     * 退出应用
     */
    public static void exitAPP() {
        sAppData.sIsExitApp = true;
        exit();

        Intent intent = new Intent();
        intent.setAction(BroadcastAction.EXIT_APP);
        sAppContext.sendBroadcast(intent);
    }

    public static void exit() {
        Toast.exit();
    }
}
