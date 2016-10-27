package com.yuelinghui.personal.widget.bannerview;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class BannerInfo implements Serializable {
    /**
     * 轮播时间间隔
     */
    public long interval;

    /**
     * 推荐位配置列表
     */
    public List<Banner> bannerData;
}
