package com.yuelinghui.personal.widget.core;

import android.content.Context;

import com.yuelinghui.personal.maframe.result.ResultCallbackAdapter;
import com.yuelinghui.personal.maframe.result.ResultNotifier;
import com.yuelinghui.personal.maframe.result.ResultNotifyTask;
import com.yuelinghui.personal.network.NetModel;

/**
 * Created by yuelinghui on 16/10/14.
 */

public class WebTextManager extends NetModel {
    /**
     * Constructor
     *
     * @param context
     */
    public WebTextManager(Context context) {
        super(context);
    }

    public WebTextManager() {
        this(null);
    }

    public void loadWebText(final String url, final ResultNotifier<String> notifier) {
        onlineExecute(new ResultNotifyTask(notifier) {
            @Override
            protected void onExecute() {
                mNetClient.loadText(url,new ResultCallbackAdapter<String>(notifier));
            }
        });
    }
}
