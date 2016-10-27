package com.yuelinghui.personal.myapplication.util;

import android.content.Context;
import android.content.Intent;

import com.yuelinghui.personal.myapplication.core.AppBroadcastAction;

/**
 * Created by yuelinghui on 16/10/17.
 */

public class BroadcastUtil {

    public static void sendNightModeChange(Context context) {
        Intent intent = new Intent();
        intent.setAction(AppBroadcastAction.NIGHT_MODE_CHANGE);
        context.sendBroadcast(intent);
    }
}
