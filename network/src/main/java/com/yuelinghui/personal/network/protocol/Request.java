package com.yuelinghui.personal.network.protocol;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Created by yuelinghui on 16/9/27.
 */

public class Request {
    /**
     * 发起http请求的实例
     */
    public HttpUriRequest httpRequest;
    /**
     * 是否可以重试
     */
    public boolean retry = true;

    public Request(HttpUriRequest httpUriRequest, boolean retry) {
        this.httpRequest = httpUriRequest;
        this.retry = retry;
    }
}
