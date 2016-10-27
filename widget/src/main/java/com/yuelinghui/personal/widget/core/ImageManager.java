package com.yuelinghui.personal.widget.core;

import android.content.Context;
import android.graphics.Bitmap;

import com.yuelinghui.personal.maframe.result.ResultCallbackAdapter;
import com.yuelinghui.personal.maframe.result.ResultNotifier;
import com.yuelinghui.personal.maframe.result.ResultNotifyTask;
import com.yuelinghui.personal.network.NetModel;

/**
 * Created by yuelinghui on 16/10/11.
 */
public class ImageManager extends NetModel{
    /**
     * Constructor
     *
     * @param context
     */
    public ImageManager(Context context) {
        super(context);
    }

    public ImageManager() {
        this(null);
    }

    public void loadImage(final String url, final ResultNotifier<Bitmap> notifier) {
        onlineExecute(new ResultNotifyTask(notifier) {
            @Override
            protected void onExecute() {
                mNetClient.loadImage(url,new ResultCallbackAdapter<Bitmap>(notifier));
            }
        });
    }

    public Bitmap loadImageFromCache(String url) {
        return mNetClient.loadImageFromCache(url);
    }

}
