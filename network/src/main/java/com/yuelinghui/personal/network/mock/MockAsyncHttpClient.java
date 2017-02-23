package com.yuelinghui.personal.network.mock;

import android.util.Log;

import com.yuelinghui.personal.maframe.concurrent.Callbackable;
import com.yuelinghui.personal.maframe.result.Result;
import com.yuelinghui.personal.maframe.result.TypedResult;
import com.yuelinghui.personal.network.protocol.CustomProtocolGroup;
import com.yuelinghui.personal.network.protocol.RequestParam;

import org.json.JSONException;

/**
 * Created by yuelinghui on 16/9/27.
 */

public class MockAsyncHttpClient {

    private static CustomProtocolGroup payProtocol = new CustomProtocolGroup();

    /**
     * client创建器
     *
     * @author liuzhiyun
     */
    private static class ClientHolder {
        public static MockAsyncHttpClient instance = new MockAsyncHttpClient();
    }

    public static MockAsyncHttpClient getInstance() {
        return ClientHolder.instance;
    }

    private MockAsyncHttpClient() {
    }

    /**
     * 和支付相关http请求构造、执行、返回处理
     *
     * @param param
     */
    public <DataType, MessageType, ControlType> TypedResult<DataType, MessageType, ControlType> payExecute(
            final RequestParam param) {
        TypedResult<DataType, MessageType, ControlType> result = new TypedResult<DataType, MessageType, ControlType>(
                Result.INTERNAL_DATA_ERROR, Result.ERROR, "no Mock action: "
                + param.getClass().getSimpleName());
        String content = null;

        try {
            payProtocol.buildPostRequest(param);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MockProtocol mp = MockConfig.getMockProtocol(param);
        if (mp != null) {
            content = mp.execute(param);
        }

        if (content != null && content.length() > 0) {
            Log.d("mock", content);
            try {
                result = payProtocol.parseResult(param, null, content);
            } catch (JSONException e) {
                result = new TypedResult<DataType, MessageType, ControlType>(
                        Result.INTERNAL_EXCEPTION, Result.ERROR, e);
            }
        }

        return result;
    }

    /**
     * 原生请求
     *
     * @param param
     */
    public String rawPayExecute(final RequestParam param) {
        String content = null;

        try {
            payProtocol.buildPostRequest(param);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MockProtocol mp = MockConfig.getMockProtocol(param);
        if (mp != null) {
            content = mp.execute(param);
        }

        Log.d("mock", content);

        return content;
    }

    /**
     * 原生请求
     *
     * @param param
     * @param callback
     */
    public void rawPayExecute(final RequestParam param,
                              final Callbackable<String> callback) {

        new Thread() {

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(1 * 1000);
                    String content = null;

                    try {
                        payProtocol.buildPostRequest(param);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    MockProtocol mp = MockConfig.getMockProtocol(param);
                    if (mp != null) {
                        content = mp.execute(param);
                    }

                    Log.d("mock", content);

                    if (callback != null) {
                        callback.callback(content);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.callback("");
                    }
                    return;
                }

            }
        }.start();
    }

    /**
     * 和支付相关http请求构造、执行、返回处理
     */
    public <DataType> void payExecute(final RequestParam param,
                                      final Callbackable<Result<DataType>> callback) {
        new Thread() {

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(1 * 1000);

                    Result<DataType> result = new Result<DataType>(
                            Result.INTERNAL_DATA_ERROR, Result.ERROR,
                            "no Mock action: "
                                    + param.getClass().getSimpleName());
                    String content = null;

                    try {
                        payProtocol.buildPostRequest(param);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    MockProtocol mp = MockConfig.getMockProtocol(param);
                    if (mp != null) {
                        content = mp.execute(param);
                    }

                    if (callback != null) {
                        if (content != null && content.length() > 0) {
                            Log.d("mock", content);
                            try {
                                result = payProtocol
                                        .parseResult(param, null, content);
                            } catch (JSONException e) {
                                result = new Result<DataType>(
                                        Result.INTERNAL_EXCEPTION,
                                        Result.ERROR, e);
                            }
                        }

                        callback.callback(result);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    if (callback != null) {
                        callback.callback(new Result<DataType>(
                                Result.INTERNAL_EXCEPTION, Result.ERROR, e));
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}
