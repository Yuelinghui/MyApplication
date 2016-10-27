package com.yuelinghui.personal.network.http;

import android.os.Handler;
import android.os.Message;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


/**
 * Created by yuelinghui on 16/9/26.
 */
public class AsyncHttpResponseHandler {
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int CANCEL_MESSAGE = 4;

    private Handler mHandler;

    public AsyncHttpResponseHandler() {
    }

    public void onStart() {
    }

    public void onFinish() {
    }

    public void onCancel() {
    }

    public void onSuccess(String content) {
    }

    public void onSuccess(int statusCode, Header[] headers, String content) {
        onSuccess(statusCode, content);
    }

    private void onSuccess(int statusCode, String content) {
        onSuccess(content);
    }

    public void onFailure(Throwable error) {
    }

    public void onFailure(Throwable error, String content) {
        onFailure(error);
    }

    protected void sendSuccessMessage(int statusCode, Header[] headers, String respinseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{new Integer(statusCode), headers, respinseBody}));
    }

    protected void sendFailureMessage(Throwable e, String responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    protected void sendFailureMessage(Throwable e, byte[] responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    protected void sendCancelMessage() {
        sendMessage(obtainMessage(CANCEL_MESSAGE, null));
    }

    protected void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    protected void sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null));
    }

    protected void handleSuccessMessage(int statusCode, Header[] headers, String responseBody) {
        onSuccess(statusCode, headers, responseBody);
    }

    protected void handleFailureMessage(Throwable e, String responseBody) {
        onFailure(e, responseBody);
    }

    protected void handleMessage(Message msg) {
        Object[] response;

        switch (msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[]) msg.obj;
                handleSuccessMessage(((Integer) response[0]).intValue(), (Header[]) response[1], (String) response[2]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                handleFailureMessage((Throwable) response[0], (String) response[1]);
                break;
            case START_MESSAGE:
                onStart();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
            case CANCEL_MESSAGE:
                onCancel();
                break;
        }
    }

    protected void sendMessage(Message msg) {
        if (mHandler != null) {
            mHandler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    Message obtainMessage(int responseMsg, Object response) {
        Message msg = null;
        if (mHandler != null) {
            msg = mHandler.obtainMessage(responseMsg, response);
        } else {
            msg = Message.obtain();
            msg.what = responseMsg;
            msg.obj = response;
        }
        return msg;
    }

    public void sendResponseMessage(HttpUriRequest request, HttpResponse response) {
        StatusLine status = response.getStatusLine();
        String responseBody = null;
        try {
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if (temp != null) {
                entity = new BufferedHttpEntity(temp);
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            sendFailureMessage(e, (String) null);
        }

        abortRequest(request);

        if (status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
        } else {
            sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), responseBody);
        }
    }

    void abortRequest(HttpUriRequest request) {
        if (request != null) {
            try {
                request.abort();
            } catch (Exception e) {
            }
        }
    }


}
