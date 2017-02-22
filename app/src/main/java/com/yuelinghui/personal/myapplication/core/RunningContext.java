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
