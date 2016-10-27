package com.yuelinghui.personal.maframe.result;

import com.yuelinghui.personal.maframe.concurrent.Callbackable;

import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class ResultCallbackAdapter<DataType> implements Callbackable<Result<DataType>> {

    private static final String ERROR_NET_EXCEPITON = "网络异常，请检查您的网络";
    private static final String ERROR_NET_CERTIFICATE = "证书校验失败，请换一个可信任的网络重试，或者访问 m.jdpay.com 官网下载最新版本.";
    private static final String ERROR_NET_SESSION = "请求中断，请检查您的网络";
    private static final String ERROR_NET_NOHOST = "服务器解析地址失败";
    private static final String ERROR_NET_TIMEOUT = "网络超时，请检查您的网络";
    private static final String ERROR_NET_RESPONSE = "请求失败，请检查您的网络";
    private static final String ERROR_NET_IMPORTANT_DATA_NULL = "网络数据解析异常";

    protected ResultNotifier<DataType> mResultNotifier = null;

    /**
     * 调用返回的次数
     */
    volatile private int mSize = 0;

    public ResultCallbackAdapter(ResultNotifier<DataType> notifier) {
        mResultNotifier = notifier;
    }

    public ResultCallbackAdapter(ResultNotifier<DataType> notifier, int size) {
        mResultNotifier = notifier;
        mSize = size;
    }

    public ResultNotifier<?> notifier() {
        return mResultNotifier;
    }

    @Override
    public void callback(Result<DataType> result) {
        if (result != null && result.existInternalError()) {
            dispatchInternalResult(result);
            notifyFinish();
            return;
        }
        dispatchResult(result);
        notifyFinish();
    }

    /**
     * 错误结果处理
     *
     * @param result
     *            忽略Result中的成功时返回的实体
     */
    public void fail(Result<?> result) {
        if (result != null) {
            // internal
            if (result.existInternalError()) {
                dispatchInternalResult(result);
                notifyFinish();
                return;
            }

            // 特殊处理（obj为空的情况，以及其他情况）
            if (result.code == Result.OK) {
                result.setInternalError(Result.INTERNAL_DATA_ERROR);

                dispatchInternalResult(result);
                notifyFinish();
                return;
            }

            // biz
            if (mResultNotifier != null) {
                if (result.code > 0) {
                    mResultNotifier.notifyVerifyFailure(result.message);
                } else {
                    // 默认系统异常处理
                    mResultNotifier.notifyFailure(result.code, result.message);
                }
            }
        }

        notifyFinish();
    }

    /**
     * 内部异常处理
     *
     * @param result
     */
    protected void dispatchInternalResult(Result<?> result) {
        switch (result.internalError()) {
            case Result.INTERNAL_INTERRUPT:
                if (mResultNotifier != null) {
                    mResultNotifier.notifyInterrupt(result.code,
                            result.message == null ? ERROR_NET_SESSION
                                    : result.message);
                }
                return;
            case Result.INTERNAL_CANCELED:
                if (mResultNotifier != null) {
                    mResultNotifier.notifyCancel();
                }
                return;
            case Result.INTERNAL_EXCEPTION: {
                // 异常处理
                String error = ERROR_NET_EXCEPITON;
                Throwable e = result.internalException();
                if (e != null) {
                    if ((e instanceof ConnectException && e.getCause() != null && e
                            .getCause() instanceof SSLHandshakeException)
                            || e instanceof SSLHandshakeException) {
                        error = ERROR_NET_CERTIFICATE;
                    } else if (e instanceof UnknownHostException) {
                        error = ERROR_NET_NOHOST;
                    } else if (e instanceof SocketTimeoutException
                            || e instanceof ConnectTimeoutException) {
                        error = ERROR_NET_TIMEOUT;
                    } else if (e instanceof SocketException
                            || e instanceof InterruptedIOException) {
                        error = ERROR_NET_EXCEPITON;
                    } else if (e instanceof HttpResponseException) {
                        error = ERROR_NET_RESPONSE;
                    }
                }
                if (mResultNotifier != null) {
                    mResultNotifier.notifyVerifyFailure(error);
                }
            }
            break;
            case Result.INTERNAL_DATA_ERROR:
                // 数据错误处理
                if (mResultNotifier != null) {
                    mResultNotifier
                            .notifyVerifyFailure(ERROR_NET_IMPORTANT_DATA_NULL);
                }
                break;
            default:
                if (mResultNotifier != null) {
                    mResultNotifier.notifyVerifyFailure(ERROR_NET_EXCEPITON);
                }
                break;
        }
    }

    /**
     * 处理通知
     *
     * @param result
     */
    protected void dispatchResult(Result<DataType> result) {
        if (mResultNotifier != null && result != null) {
            switch (result.code) {
                case Result.OK:
                    mResultNotifier.notifySuccess(result.obj, result.message);
                    result.obj = null;
                    break;
                case Result.NEXT_SMS:
                    mResultNotifier.notifySMS(result.obj, result.message);
                    result.obj = null;
                    break;
                case Result.WAITING:
                    mResultNotifier.notifyWaiting(result.message);
                    result.obj = null;
                    break;
                default:
                    if (result.code > 0) {
                        mResultNotifier.notifyVerifyFailure(result.message);
                    } else {
                        // 默认系统异常处理
                        mResultNotifier.notifyFailure(result.code, result.message);
                    }
                    break;
            }
        }
    }

    /**
     * 线程都完成时调用finish
     */
    protected void notifyFinish() {
        if (mResultNotifier != null
                && mResultNotifier instanceof MultiResultHandler) {
            if (--mSize <= 0) {
                mResultNotifier.notifyFinish();
            }

        }
    }
}
