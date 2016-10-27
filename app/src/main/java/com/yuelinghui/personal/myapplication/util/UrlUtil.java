package com.yuelinghui.personal.myapplication.util;

import com.yuelinghui.personal.myapplication.core.AppRunningContext;

/**
 * Created by yuelinghui on 16/9/30.
 */

public class UrlUtil {

    public static String lastUrl(String url) {
        return AppRunningContext.URL_BASE + url;
    }

    public static String detailUrl() {
        return AppRunningContext.URL_BASE;
    }

    public static String beforeUrl(String url) {
        return AppRunningContext.URL_BASE + url;
    }
}
