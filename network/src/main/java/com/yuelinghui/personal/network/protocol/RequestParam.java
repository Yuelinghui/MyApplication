package com.yuelinghui.personal.network.protocol;

/**
 * Created by yuelinghui on 16/9/27.
 */

public abstract class RequestParam {


    /**
     * 加密敏感信息流程控制
     */
    public final void encrypt() {
        onEncrypt();
    }

    /**
     * 具体敏感信息加密实现，子类继承实现
     */
    protected void onEncrypt() {
    }

    /**
     * request打包，比如转换，加密等的操作
     *
     * @param content
     *            打包前的字符串，比如：未加密字符串
     * @return 打包后的字符串，比如：已加密字符串
     */
    public String pack(String content) {
        return content;
    }

    /**
     * response拆包，比如转换，解密等的操作
     *
     * @param content
     *            拆包钱的字符串，比如：加密字符串
     * @return 拆包后的字符串，比如：已解密字符串
     */
    public String unpack(String content) {
        return content;
    }

}
