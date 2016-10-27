package com.yuelinghui.personal.maframe.concurrent;

/**
 * Created by yuelinghui on 16/8/30.
 */
public interface Callbackable<Result> {

    void callback(Result result);
}
