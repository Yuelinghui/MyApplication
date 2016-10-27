package com.yuelinghui.personal.network;

import android.content.Context;

import com.yuelinghui.personal.maframe.result.ResultCallbackAdapter;
import com.yuelinghui.personal.maframe.result.ResultNotifier;
import com.yuelinghui.personal.maframe.result.ResultNotifyTask;
import com.yuelinghui.personal.maframe.step.StepLine;
import com.yuelinghui.personal.network.protocol.RequestParam;
import com.yuelinghui.personal.network.protocol.UrlParam;

import java.util.ArrayList;

/**
 * Created by yuelinghui on 16/9/27.
 */

public class NetModel {


    protected NetClient mNetClient = null;
    protected Context mContext = null;

    /**
     * Constructor
     *
     * @param context
     */
    protected NetModel(Context context) {
        mContext = context;
        mNetClient = new NetClient(context);
    }

    /**
     * 取消上下文相关的执行操作
     */
    public void cancel() {
        cancel(mContext);
    }

    /**
     * 取消上下文相关的执行操作，用于外部多Model操作
     *
     * @param context
     */
    public static void cancel(Context context) {
        NetClient.cancelExecute(context);
    }

    /**
     * 执行在线任务
     *
     * @param param
     * @param notifier
     */
    protected <DataType> void onlineExecute(final RequestParam param,
                                            final ResultNotifier<DataType> notifier) {
        if (!isValidRequest(param)) {
            return;
        }

        new ResultNotifyTask(notifier) {

            @Override
            protected void onExecute() {
                mNetClient.postExecute(param,
                        new ResultCallbackAdapter<DataType>(notifier));
            }

        }.execute(mContext);
    }

    protected <DataType> void onlineGet(final RequestParam requestParam, final UrlParam urlParam, final ResultNotifier<DataType> notifier) {
        new ResultNotifyTask(notifier) {

            @Override
            protected void onExecute() {
                mNetClient.getExecute(requestParam, urlParam, new ResultCallbackAdapter<DataType>(notifier));
            }
        }.execute(mContext);
    }

    /**
     * 执行在线任务
     *
     * @param param
     * @param adapter
     */
    protected void onlineExecute(final RequestParam param,
                                 final ResultCallbackAdapter<?> adapter) {
        if (!isValidRequest(param)) {
            return;
        }

        new ResultNotifyTask(adapter.notifier()) {

            @Override
            protected void onExecute() {
                mNetClient.postExecute(param, adapter);
            }

        }.execute(mContext);
    }

    /**
     * 执行在线任务，不支持请求合法性判断
     */
    protected void onlineExecute(ResultNotifyTask notifyTask) {
        if (notifyTask != null) {
            notifyTask.execute(mContext);
        }
    }

    /**
     * 返回多条结果
     *
     * @param paramList
     * @param notifier
     */
    @SuppressWarnings("unchecked")
    protected void onlinePayExcute(ArrayList<RequestParam> paramList,
                                   final ResultNotifier<Object> notifier) {
        if (notifier == null) {
            return;
        }
        // 调用onStart
        if (!notifier.prepare(null)) {
            notifier.notifyFinish();
            return;
        }

        // 入参为空时结束
        if (paramList == null || paramList.size() <= 0) {
            notifier.notifyFinish();
            return;
        }
        @SuppressWarnings("rawtypes")
        final ResultCallbackAdapter callbackAdapter = new ResultCallbackAdapter<Object>(
                notifier, paramList.size());
        for (final RequestParam param : paramList) {
            mNetClient.postExecute(param, callbackAdapter);
        }
    }

    /**
     * 返回多条结果
     *
     * @param paramList
     */
    @SuppressWarnings("unchecked")
    protected void onlinePayExcute(ArrayList<RequestParam> paramList,
                                   final ResultCallbackAdapter<Object> callbackAdapter) {
        if (callbackAdapter == null) {
            return;
        }
        ResultNotifier<Object> notifier = (ResultNotifier<Object>) callbackAdapter
                .notifier();
        if (notifier == null) {
            return;
        }
        // 调用onStart
        if (!notifier.prepare(null)) {
            notifier.notifyFinish();
            return;
        }

        // 入参为空时结束
        if (paramList == null || paramList.size() <= 0) {
            notifier.notifyFinish();
            return;
        }
        for (final RequestParam param : paramList) {
            mNetClient.postExecute(param, callbackAdapter);
        }
    }

    /**
     * 是否合法的请求
     *
     * @param param
     * @return true - 合法请求; false - 不合法的请求（如：段时间内重复的请求）
     */
    private boolean isValidRequest(final RequestParam param) {
        if (NetClient.isRepeatableRequest(param)) {
            return true;
        }

        return StepLine.getInstance().add(param.getClass().getCanonicalName());
    }


}
