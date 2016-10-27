package com.yuelinghui.personal.network.protocol;

/**
 * Created by yuelinghui on 16/9/27.
 */

public abstract class CacheRequestParam extends RequestParam {

    /**
     * 缓存时间
     */
    public String sysDataTime = null;

    /**
     * 获取唯一标识
     * @return
     */
    public abstract String getCacheId();

}
