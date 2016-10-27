package com.yuelinghui.personal.myapplication.home.model;

import android.content.Context;

import com.yuelinghui.personal.maframe.result.ResultNotifier;
import com.yuelinghui.personal.myapplication.home.entity.ArticleInfo;
import com.yuelinghui.personal.myapplication.home.entity.HomeDetailInfo;
import com.yuelinghui.personal.myapplication.home.protocol.BeforeParam;
import com.yuelinghui.personal.myapplication.home.protocol.DetailParam;
import com.yuelinghui.personal.myapplication.home.protocol.LastParam;
import com.yuelinghui.personal.myapplication.home.protocol.MainProtocol;
import com.yuelinghui.personal.network.NetClient;
import com.yuelinghui.personal.network.NetModel;

/**
 * Created by yuelinghui on 16/9/30.
 */

public class MainModel extends NetModel {

    static {
        NetClient.addProtocol(new MainProtocol());
    }

    public void queryLast(ResultNotifier<ArticleInfo> notifier) {
        LastParam param = new LastParam();

        onlineGet(param, null, notifier);
    }

    public void querDetail(int id, ResultNotifier<HomeDetailInfo> notifier) {
        DetailParam detailParam = new DetailParam();
        detailParam.id = id;
        onlineGet(null, detailParam, notifier);
    }

    public void queryBefore(String beforeDate, ResultNotifier<ArticleInfo> notifier) {
        BeforeParam param = new BeforeParam();
        param.beforeDate = beforeDate;
        onlineGet(null, param, notifier);
    }

    /**
     * Constructor
     *
     * @param context
     */
    public MainModel(Context context) {
        super(context);
    }
}
