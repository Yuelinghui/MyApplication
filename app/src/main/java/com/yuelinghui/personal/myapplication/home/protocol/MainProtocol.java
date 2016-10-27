package com.yuelinghui.personal.myapplication.home.protocol;

import com.yuelinghui.personal.myapplication.home.entity.HomeDetailInfo;
import com.yuelinghui.personal.myapplication.util.UrlUtil;
import com.yuelinghui.personal.myapplication.home.entity.ArticleInfo;
import com.yuelinghui.personal.network.protocol.CPProtocol;
import com.yuelinghui.personal.network.protocol.CPProtocolAction;
import com.yuelinghui.personal.network.protocol.CPProtocolGroup;

/**
 * Created by yuelinghui on 16/9/30.
 */

public class MainProtocol implements CPProtocol {

    static {
        CPProtocolGroup.addAction(LastParam.class, new CPProtocolAction(UrlUtil.lastUrl("/latest"), ArticleInfo.class));
        CPProtocolGroup.addAction(DetailParam.class,new CPProtocolAction(UrlUtil.detailUrl(), HomeDetailInfo.class));
        CPProtocolGroup.addAction(BeforeParam.class, new CPProtocolAction(UrlUtil.lastUrl("/before"), ArticleInfo.class));
    }

    @Override
    public void load(CPProtocolGroup protocolGroup) {

    }
}
