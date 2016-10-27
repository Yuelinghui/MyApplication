package com.yuelinghui.personal.network.http;

import android.os.SystemClock;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;

import javax.net.ssl.SSLException;

/**
 * Created by yuelinghui on 16/9/26.
 */

public class RetryHandler implements HttpRequestRetryHandler {
    private static final int RETRY_SLEEP_TIME_MILLIS = 1500;
    private static HashSet<Class<?>> mExceptionWhiteList = new HashSet<Class<?>>();
    private static HashSet<Class<?>> mExceptionBlackList = new HashSet<Class<?>>();

    static {
        mExceptionWhiteList.add(NoHttpResponseException.class);
        mExceptionWhiteList.add(UnknownHostException.class);
        mExceptionWhiteList.add(SocketException.class);

        mExceptionBlackList.add(InterruptedIOException.class);
        mExceptionBlackList.add(SSLException.class);
    }

    private final int mMaxRetries;

    public RetryHandler(int maxRetries) {
        this.mMaxRetries = maxRetries;
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext httpContext) {
        boolean retry = true;

        Boolean b = (Boolean) httpContext.getAttribute(ExecutionContext.HTTP_REQ_SENT);
        boolean sent = (b != null && b.booleanValue());

        if (executionCount > mMaxRetries) {
            retry = false;
        } else if (isInList(mExceptionBlackList, exception)) {
            retry = false;
        } else if (isInList(mExceptionWhiteList, exception)) {
            retry = true;
        } else if (!sent) {
            retry = true;
        }

        if (retry) {
            HttpUriRequest currentReq = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
            String requestType = currentReq.getMethod();
            retry = !requestType.equals("POST");
        }
        if (retry) {
            SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
        }
        return retry;
    }

    protected boolean isInList(HashSet<Class<?>> list, Throwable error) {
        Iterator<Class<?>> itr = list.iterator();
        while (itr.hasNext()) {
            if (itr.next().isInstance(error)) {
                return true;
            }
        }
        return false;
    }

    public boolean retryConnection(int executionCount) {
        if (executionCount <= mMaxRetries) {
            SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
            return true;
        }
        return false;
    }
}
