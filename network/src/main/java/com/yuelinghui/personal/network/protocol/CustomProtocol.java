package com.yuelinghui.personal.network.protocol;

/**
 * Created by yuelinghui on 16/9/27.
 */
public interface CustomProtocol {
    /**
     * 装在协议到协议组
     * @param protocolGroup
     */
    void load(CustomProtocolGroup protocolGroup);
}
