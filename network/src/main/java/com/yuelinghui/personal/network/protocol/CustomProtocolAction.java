package com.yuelinghui.personal.network.protocol;

import java.lang.reflect.Type;

/**
 * Created by yuelinghui on 16/9/27.
 */
public class CustomProtocolAction {
    /**
     * 请求地址
     */
    public String url;
    /**
     * 请求重试
     */
    public boolean retry;
    /**
     * 结果对象类型
     */
    public Type resultType;
    /**
     * 消息对象类型
     */
    public Type messageType;
    /**
     * 流程控制对象类型
     */
    public Type controlType;

    public CustomProtocolAction(String url, Type resultType) {
        this(url, true, resultType, String.class, Void.class);
    }

    public CustomProtocolAction(String url, Type resultType, Type messageType,
                                Type controlType) {
        this(url, true, resultType, messageType, controlType);
    }

    public CustomProtocolAction(String url, boolean retry, Type resultType) {
        this(url, retry, resultType, String.class, Void.class);
    }

    public CustomProtocolAction(String url, boolean retry, Type resultType,
                                Type messageType, Type controlType) {
        this.url = url;
        this.retry = retry;
        this.resultType = resultType;
        this.messageType = messageType;
        this.controlType = controlType;
    }
}
