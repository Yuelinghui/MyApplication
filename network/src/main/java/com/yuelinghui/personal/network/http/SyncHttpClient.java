package com.yuelinghui.personal.network.http;

import android.content.Context;
import android.os.Message;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Created by yuelinghui on 16/9/27.
 */

public class SyncHttpClient extends AsyncHttpClient {

    private int responseCode;
    /*
     * as this is a synchronous request this is just a helping mechanism to pass
     * the result back to this method. Therefore the result object has to be a
     * field to be accessible
     */
    protected String result;
    protected AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void sendResponseMessage(HttpUriRequest request,
                                        org.apache.http.HttpResponse response) {
            responseCode = response.getStatusLine().getStatusCode();
            super.sendResponseMessage(request, response);
        };

        @Override
        protected void sendMessage(Message msg) {
			/*
			 * Dont use the handler and send it directly to the analysis
			 * (because its all the same thread)
			 */
            handleMessage(msg);
        }

        @Override
        public void onSuccess(String content) {
            result = content;
        }

        @Override
        public void onFailure(Throwable error, String content) {
            result = onRequestFailed(error, content);
        }
    };

    /**
     * @return the response code for the last request, might be usefull
     *         sometimes
     */
    public int getResponseCode() {
        return responseCode;
    }

    // Private stuff
    @Override
    protected void sendRequest(ManagedHttpClient client,
                               boolean retry, HttpUriRequest uriRequest,
                               AsyncHttpResponseHandler responseHandler, Context context) {
		/*
		 * will execute the request directly
		 */
        new AsyncHttpRequest(this, client, retry, uriRequest,
                responseHandler).run();
    }

    public String onRequestFailed(Throwable error, String content) {
        // by firefox
        return null;
    }

    public void delete(String url, RequestParams queryParams,
                       AsyncHttpResponseHandler responseHandler) {
        // TODO what about query params??
        delete(url, responseHandler);
    }

    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        this.get(url, null, responseHandler);
    }

    public String get(String url, RequestParams params) {
        this.get(url, params, responseHandler);
		/*
		 * the response handler will have set the result when this line is
		 * reached
		 */
        return result;
    }

    public String get(String url) {
        this.get(url, null, responseHandler);
        return result;
    }

    public String put(String url, RequestParams params) {
        this.put(url, params, responseHandler);
        return result;
    }

    public String put(String url) {
        this.put(url, null, responseHandler);
        return result;
    }

    public String post(String url, RequestParams params) {
        this.post(url, params, responseHandler);
        return result;
    }

    public String post(String url) {
        this.post(url, null, responseHandler);
        return result;
    }

    public String delete(String url, RequestParams params) {
        this.delete(url, params, responseHandler);
        return result;
    }

    public String delete(String url) {
        this.delete(url, null, responseHandler);
        return result;
    }

    /**
     * Perform a HTTP request and track the Android Context which initiated the
     * request. Set headers/entities/contentType only for this request
     *
     * @param request
     *            a raw {@link HttpUriRequest} to send, for example, use this to
     *            {@link HttpGet}/{@link HttpPost} string/json/xml payloads to a
     *            server(url) by passing a
     *            {@link org.apache.http.entity.StringEntity}. and set
     *            headers(contentType) for this request
     */
    public String send(HttpUriRequest request) {
        this.send(null, true, request, responseHandler);
        return result;
    }
}
