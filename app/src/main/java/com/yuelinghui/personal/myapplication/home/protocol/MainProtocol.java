package com.yuelinghui.personal.myapplication.home.protocol;

import com.yuelinghui.personal.myapplication.home.entity.HomeDetailInfo;
import com.yuelinghui.personal.myapplication.util.UrlUtil;
import com.yuelinghui.personal.myapplication.home.entity.ArticleInfo;
import com.yuelinghui.personal.network.protocol.CustomProtocol;
import com.yuelinghui.personal.network.protocol.CustomProtocolAction;
import com.yuelinghui.personal.network.protocol.CustomProtocolGroup;

/**
 * Created by yuelinghui on 16/9/30.
 */

public class MainProtocol implements CustomProtocol {

    static {
        CustomProtocolGroup.addAction(LastParam.class, new CustomProtocolAction(UrlUtil.lastUrl("/latest"), ArticleInfo.class));
        CustomProtocolGroup.addAction(DetailParam.class,new CustomProtocolAction(UrlUtil.detailUrl(), HomeDetailInfo.class));
        CustomProtocolGroup.addAction(BeforeParam.class, new CustomProtocolAction(UrlUtil.lastUrl("/before"), ArticleInfo.class));
    }

    @Override
    public void load(CustomProtocolGroup protocolGroup) {

    }
}
