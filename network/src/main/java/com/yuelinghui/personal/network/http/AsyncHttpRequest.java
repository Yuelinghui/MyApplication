package com.yuelinghui.personal.network.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

/**
 * Created by yuelinghui on 16/9/27.
 */
public class AsyncHttpRequest implements Runnable {
    private final AsyncHttpClient mClientManager;
    private ManagedHttpClient mClient;
    private final HttpUriRequest mRequest;
    private final AsyncHttpResponseHandler mResponseHandler;
    private final boolean mExecutionRetry;
    private boolean isBinaryRequest;
    private int mExecutionCount;

    public AsyncHttpRequest(AsyncHttpClient asyncHttpClient, ManagedHttpClient client, boolean retry, HttpUriRequest uriRequest, AsyncHttpResponseHandler responseHandler) {
        this.mClientManager = asyncHttpClient;
        this.mClient = client;
        this.mExecutionRetry = retry;
        this.mRequest = uriRequest;
        this.mResponseHandler = responseHandler;
        if (responseHandler instanceof BinaryHttpResponseHandler) {
            this.isBinaryRequest = true;
        }
    }

    @Override
    public void run() {
        try {

            cleanConnections();

            if (mResponseHandler != null) {
                mResponseHandler.sendStartMessage();
            }

            makeRequestWithRetries();

            if (mResponseHandler != null) {
                mResponseHandler.sendFinishMessage();
            }
        } catch (IOException e) {
            if (mResponseHandler != null) {
                if (this.isBinaryRequest) {
                    mResponseHandler.sendFailureMessage(e,(byte[]) null);
                } else {
                    mResponseHandler.sendFailureMessage(e,(String) null);
                }
                mResponseHandler.sendFinishMessage();
            }
        }


    }

    private void makeRequestWithRetries() throws ConnectException {
        boolean retry = true;
        boolean firstException = true;
        Exception cause = null;

        while (retry) {
            try {
                makeRequest();
                return;
            } catch (IllegalStateException e) {
                if (mClient.isShutDown && firstException && changeClient()) {
                    firstException = false;
                    continue;
                }
                abortRequest();
                if (mResponseHandler != null) {
                    mResponseHandler.sendFailureMessage(e, "connection pool shut down");
                    return;
                }
            } catch (UnknownHostException e) {
                abortRequest();
                if (mResponseHandler != null) {
                    mResponseHandler.sendFailureMessage(e, "can't resolve host");
                    return;
                }
            } catch (SSLHandshakeException e) {
                if (firstException && changeClient()) {
                    firstException = false;
                    continue;
                }
                abortRequest();
                if (mResponseHandler != null) {
                    mResponseHandler.sendFailureMessage(e, "can,t hands shake");
                    return;
                }
            } catch (SocketException e) {
                retry = retryWhenException(e);
                if (!retry) {
                    abortRequest();
                    if (mResponseHandler != null) {
                        mResponseHandler.sendFailureMessage(e, "can,t resolve host");
                        return;
                    }
                }
            } catch (SocketTimeoutException e) {
                retry = retryWhenException(e);
                if (!retry) {
                    abortRequest();
                    if (mResponseHandler != null) {
                        mResponseHandler.sendFailureMessage(e, "socket time out");
                        return;
                    }
                }
            } catch (ConnectTimeoutException e) {
                retry = retryWhenException(e);
                if (!retry) {
                    abortRequest();
                    if (mResponseHandler != null) {
                        mResponseHandler.sendFailureMessage(e, "connect time out");
                        return;
                    }
                }
            } catch (InterruptedIOException e) {
                retry = retryWhenException(e);
                if (!retry) {
                    abortRequest();
                    if (mResponseHandler != null) {
                        mResponseHandler.sendFailureMessage(e, "connect to not exist host time out");
                        return;
                    }
                }
            } catch (IOException e) {
                retry = retryWhenException(e);
                if (!retry) {
                    abortRequest();
                    if (mResponseHandler != null) {
                        mResponseHandler.sendFailureMessage(e, "IO Exception");
                        return;
                    }
                }
            } catch (NullPointerException e) {
                retry = retryWhenException(new IOException("NPE in HttpClient" + e.getMessage()));
                if (!retry) {
                    abortRequest();
                    if (mResponseHandler != null) {
                        mResponseHandler.sendFailureMessage(e, "Null Pointer Exception");
                        return;
                    }
                }
            } catch (Exception e) {
                cause = e;
                retry = false;
            }
        }

        abortRequest();
        ConnectException ex = new ConnectException();
        ex.initCause(cause);
        throw ex;
    }

    /**
     * 判断异常是否需要重试
     *
     * @param e
     * @return
     */
    private boolean retryWhenException(IOException e) {
        boolean retry = false;
        HttpRequestRetryHandler retryHandler = mClient.getHttpRequestRetryHandler();
        if (mExecutionRetry) {
            if (mExecutionCount == 1) {
                mClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
            } else if (mExecutionCount == 2) {
                changeClient();
            }
            ++mExecutionCount;
            retry = retryHandler.retryRequest(e, mExecutionCount, mClient.httpContext);
        }
        return retry;
    }

    /**
     * 切换client对象
     *
     * @return 是否切换成功
     */
    private boolean changeClient() {
        ManagedHttpClient oldClient = mClient;
        mClient = mClientManager.changeHttpClient(mClient);
        return oldClient != mClient;
    }

    /**
     * 终止请求
     */
    private void abortRequest() {
        try {
            mRequest.abort();
        } catch (Exception e) {
        }
    }

    private void makeRequest() throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            try {
                HttpResponse response = mClient.execute(mRequest, mClient.httpContext);
                if (!Thread.currentThread().isInterrupted()) {
                    if (mResponseHandler != null) {
                        mResponseHandler.sendResponseMessage(mRequest, response);
                    }
                } else {
                    //TODO: should raise InterruptedException? this block is reached whenever the request is cancelled before its response is received
                    if (mResponseHandler != null) {
                        mResponseHandler.sendCancelMessage();
                    }
                }
                abortRequest();
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    throw e;
                } else {
                    if (mResponseHandler != null) {
                        mResponseHandler.sendCancelMessage();
                    }
                }
            }
        }
    }

    /**
     * 整理现有链接
     */
    private void cleanConnections() {
        try {
            mClient.getConnectionManager().closeExpiredConnections();
        } catch (Exception e) {
        }
    }
}
