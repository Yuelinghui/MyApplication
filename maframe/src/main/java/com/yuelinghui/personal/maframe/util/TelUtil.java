package com.yuelinghui.personal.maframe.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class TelUtil {

    /**
     * 跳转到拨号页面
     *
     * @param context
     * @param mobile
     * @author wyqiuchunlong
     */
    public static void dial(Context context, String mobile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobile));
        context.startActivity(intent);
    }

    /**
     * 直接拨打电话号码
     *
     * @param context
     * @param mobile
     * @author wyqiuchunlong
     */
    public static void call(Context context, String mobile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile));
        context.startActivity(intent);
    }

}
