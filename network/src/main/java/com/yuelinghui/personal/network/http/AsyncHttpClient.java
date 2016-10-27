package com.yuelinghui.personal.network.http;

import android.content.Context;
import android.text.TextUtils;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

/**
 * Created by yuelinghui on 16/9/26.
 */

public class AsyncHttpClient {

    /**
     * 最大连接数
     */
    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    /**
     * 超时时间
     */
    private static final int DEFAULT_SOCKET_TIMEOUT = 20 * 1000;
    /**
     * 重加载次数
     */
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024;
    private static final String HEADER_ACCEPT_ENCODING = "Accept_Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static int mMaxConnections = DEFAULT_MAX_CONNECTIONS;
    private static int mSocketTimeOut = DEFAULT_SOCKET_TIMEOUT;

    private SSLSocketFactory mSSLSocketFactory;
    private final RetryHandler mRetryHandler;
    private ThreadPoolExecutor mThreadPool;
    private final Map<Context, List<WeakReference<Future<?>>>> mRequestMap;
    private final Map<String, String> mClientHeaderMap;

    private ManagedHttpClient mHttpClient;
    private byte[] mHttpClientLock = new byte[0];

    protected String syncResponse;

    public AsyncHttpClient() {
        this(DEFAULT_MAX_CONNECTIONS, mMaxConnections, DEFAULT_MAX_RETRIES);
    }

    public AsyncHttpClient(int maxTotalConnections, int maxConnectionsPerRpute, int maxRetries) {
        mSSLSocketFactory = SSLSocketFactory.getSocketFactory();
        mRetryHandler = new RetryHandler(maxRetries);
        mThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        mRequestMap = new WeakHashMap<>();
        mClientHeaderMap = new HashMap<>();

        synchronized (mHttpClientLock) {
            HttpParams params = getHttpParams(maxTotalConnections, maxConnectionsPerRpute);
            mHttpClient = createHttpClient(null, params);
        }
    }

    /**
     * 获取HttpParams
     *
     * @param maxTotalConnections
     * @param maxConnectionsPerRoute
     * @return
     */
    private HttpParams getHttpParams(int maxTotalConnections, int maxConnectionsPerRoute) {
        BasicHttpParams params = new BasicHttpParams();

        // 设置超时时间和最大连接数
        ConnManagerParams.setTimeout(params, mSocketTimeOut);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(maxConnectionsPerRoute));
        ConnManagerParams.setMaxTotalConnections(params, maxTotalConnections);

        // 设置超时时间
        HttpConnectionParams.setConnectionTimeout(params, mSocketTimeOut);

        HttpConnectionParams.setSoTimeout(params, mSocketTimeOut);
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, DEFAULT_SOCKET_BUFFER_SIZE);

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(params, "android");

        HttpClientParams.setRedirecting(params, false);

        return params;
    }

    private ManagedHttpClient createHttpClient(AbstractHttpClient reference, HttpParams params) {
        if (reference == null && params == null) {
            throw new IllegalArgumentException("referce and params can not both be null");
        }

        if (params == null) {
            params = reference.getParams();
        }

        ThreadSafeClientConnManager cm = getThreadSafeClientConnNamager(params);
        ManagedHttpClient newClient = new ManagedHttpClient(cm, params);
        initHttp(newClient);
        return newClient;
    }

    /**
     * 获取ThreadSafeClientConnManager
     *
     * @param params
     * @return
     */
    private ThreadSafeClientConnManager getThreadSafeClientConnNamager(HttpParams params) {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        if (!(mSSLSocketFactory instanceof SSLKeySetSocketFactory)) {
            try {
                mSSLSocketFactory = new SSLKeySetSocketFactory(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        schemeRegistry.register(new Scheme("https", mSSLSocketFactory, 443));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        return cm;
    }

    /**
     * 初始化Http
     *
     * @param client
     */
    private void initHttp(AbstractHttpClient client) {
        client.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
                for (String header : mClientHeaderMap.keySet()) {
                    httpRequest.addHeader(header, mClientHeaderMap.get(header));
                }
                if (HttpPost.METHOD_NAME.equals(httpRequest.getRequestLine().getMethod())) {
                    httpRequest.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
                final HttpEntity entity = httpResponse.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            httpResponse.setEntity(new InflatingEntity(httpResponse.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        client.setHttpRequestRetryHandler(mRetryHandler);

        // 避免重定向
        client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
    }

    /**
     * Overrides the threadpool implementation used when queuing/pooling
     * requests. By default, Executors.newCachedThreadPool() is used.
     *
     * @param threadPool an instance of {@link ThreadPoolExecutor} to use for queuing/pooling requests.
     */
    public void setThreadPool(ThreadPoolExecutor threadPool) {
        this.mThreadPool = threadPool;
    }

    public void setUserAgent(String userAgent) {
        synchronized (mHttpClientLock) {
            HttpProtocolParams.setUserAgent(this.mHttpClient.getParams(), userAgent);
        }
    }

    public void setTimeout(int timeout) {
        synchronized (mHttpClientLock) {
            final HttpParams params = this.mHttpClient.getParams();
            ConnManagerParams.setTimeout(params, timeout);
            HttpConnectionParams.setSoTimeout(params, timeout);
            HttpConnectionParams.setConnectionTimeout(params, timeout);
        }
    }

    public void setConnectionTimeout(int socketTimeout) {
        synchronized (mHttpClientLock) {
            final HttpParams httpParams = this.mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
        }
    }

    @Deprecated
    public void setSSLSocketFactory(SSLSocketFactory factory) {
        assert (mSSLSocketFactory instanceof SSLKeySetSocketFactory);

        addSSLKey(factory);
    }

    private boolean addSSLKey(SSLSocketFactory factory) {
        boolean flag = false;
        Field[] fields = factory.getClass().getDeclaredFields();
        for (Field byteField : fields) {
            try {
                if (byteField.getType() != byte[].class) {
                    continue;
                }
                byteField.setAccessible(true);
            } catch (Exception e) {
                byteField = null;
            }

            try {
                if (byteField != null) {
                    byte[] key = (byte[]) byteField.get(factory);
                    if (key != null) {
                        addSSLKey(key);
                        flag = true;
                    }
                }
            } catch (Exception e) {
            } finally {
                if (byteField != null) {
                    try {
                        byteField.setAccessible(false);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return flag;
    }

    public void addSSLKey(byte[] sslKey) {
        if (mSSLSocketFactory != null && mSSLSocketFactory instanceof SSLKeySetSocketFactory) {
            ((SSLKeySetSocketFactory) mSSLSocketFactory).addSSLKey(sslKey);
        }
    }

    public void addHeader(String header, String value) {
        mClientHeaderMap.put(header, value);
    }

    public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
        List<WeakReference<Future<?>>> requestList = mRequestMap.get(context);
        if (requestList != null) {
            for (WeakReference<Future<?>> requestRef : requestList) {
                Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
        mRequestMap.remove(context);
    }

    /**
     * Perform a HTTP GET request, without any parameters.
     *
     * @param url             the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        get(null, url, null, responseHandler);
    }

    /**
     * Perform a HTTP GET request with parameters.
     *
     * @param url             the URL to send the request to.
     * @param params          additional GET parameters to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        get(null, url, params, responseHandler);
    }

    /**
     * Perform a HTTP GET request without any parameters and track the Android Context which initiated the request.
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        get(context, url, null, responseHandler);
    }

    /**
     * Perform a HTTP GET request and track the Android Context which initiated the request.
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param params          additional GET parameters to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        sendRequest(mHttpClient, true, new HttpGet(getUrlWithQueryString(url, params)), null, responseHandler, context);
    }

    public void post(String url, AsyncHttpResponseHandler responseHandler) {
        post(null, url, null, responseHandler);
    }

    /**
     * Perform a HTTP POST request with parameters.
     *
     * @param url             the URL to send the request to.
     * @param params          additional POST parameters or files to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        post(null, url, params, responseHandler);
    }

    /**
     * Perform a HTTP POST request and track the Android Context which initiated the request.
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param params          additional POST parameters or files to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        post(context, url, paramsToEntity(params), null, responseHandler);
    }

    /**
     * Perform a HTTP POST request and track the Android Context which initiated the request.
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param entity          a raw {@link HttpEntity} to send with the request, for example, use this to send string/json/xml payloads to a server by passing a {@link org.apache.http.entity.StringEntity}.
     * @param contentType     the content type of the payload you are sending, for example application/json if sending a json payload.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void post(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
        sendRequest(mHttpClient, true, addEntityToRequestBase(new HttpPost(url), entity), contentType, responseHandler, context);
    }

    public void post(Context context, String url, Header[] headers, RequestParams params, String contentType,
                     AsyncHttpResponseHandler responseHandler) {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        if (params != null) request.setEntity(paramsToEntity(params));
        if (headers != null) request.setHeaders(headers);
        sendRequest(mHttpClient, true, request, contentType,
                responseHandler, context);
    }

    /**
     * Perform a HTTP POST request and track the Android Context which initiated
     * the request. Set headers only for this request
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param headers         set headers only for this request
     * @param entity          a raw {@link HttpEntity} to send with the request, for
     *                        example, use this to send string/json/xml payloads to a server by
     *                        passing a {@link org.apache.http.entity.StringEntity}.
     * @param contentType     the content type of the payload you are sending, for
     *                        example application/json if sending a json payload.
     * @param responseHandler the response handler instance that should handle
     *                        the response.
     */
    public void post(Context context, String url, Header[] headers, HttpEntity entity, String contentType,
                     AsyncHttpResponseHandler responseHandler) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPost(url), entity);
        if (headers != null) request.setHeaders(headers);
        sendRequest(mHttpClient, true, request, contentType, responseHandler, context);
    }

    /**
     * Perform a HTTP PUT request, without any parameters.
     *
     * @param url             the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(String url, AsyncHttpResponseHandler responseHandler) {
        put(null, url, null, responseHandler);
    }

    /**
     * Perform a HTTP PUT request with parameters.
     *
     * @param url             the URL to send the request to.
     * @param params          additional PUT parameters or files to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        put(null, url, params, responseHandler);
    }

    /**
     * Perform a HTTP PUT request and track the Android Context which initiated the request.
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param params          additional PUT parameters or files to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        put(context, url, paramsToEntity(params), null, responseHandler);
    }

    /**
     * Perform a HTTP PUT request and track the Android Context which initiated the request.
     * And set one-time headers for the request
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param entity          a raw {@link HttpEntity} to send with the request, for example, use this to send string/json/xml payloads to a server by passing a {@link org.apache.http.entity.StringEntity}.
     * @param contentType     the content type of the payload you are sending, for example application/json if sending a json payload.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
        sendRequest(mHttpClient, true, addEntityToRequestBase(new HttpPut(url), entity), contentType, responseHandler, context);
    }

    /**
     * Perform a HTTP PUT request and track the Android Context which initiated the request.
     * And set one-time headers for the request
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param headers         set one-time headers for this request
     * @param entity          a raw {@link HttpEntity} to send with the request, for example, use this to send string/json/xml payloads to a server by passing a {@link org.apache.http.entity.StringEntity}.
     * @param contentType     the content type of the payload you are sending, for example application/json if sending a json payload.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(Context context, String url, Header[] headers, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPut(url), entity);
        if (headers != null) request.setHeaders(headers);
        sendRequest(mHttpClient, true, request, contentType, responseHandler, context);
    }

    //
    // HTTP DELETE Requests
    //

    /**
     * Perform a HTTP DELETE request.
     *
     * @param url             the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void delete(String url, AsyncHttpResponseHandler responseHandler) {
        delete(null, url, responseHandler);
    }

    /**
     * Perform a HTTP DELETE request.
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void delete(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        final HttpDelete delete = new HttpDelete(url);
        sendRequest(mHttpClient, true, delete, null, responseHandler, context);
    }

    /**
     * Perform a HTTP DELETE request.
     *
     * @param context         the Android Context which initiated the request.
     * @param url             the URL to send the request to.
     * @param headers         set one-time headers for this request
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void delete(Context context, String url, Header[] headers, AsyncHttpResponseHandler responseHandler) {
        final HttpDelete delete = new HttpDelete(url);
        if (headers != null) delete.setHeaders(headers);
        sendRequest(mHttpClient, true, delete, null, responseHandler, context);
    }

    /**
     * Perform a HTTP request and track the Android Context which initiated
     * the request. Set headers/entities/contentType only for this request
     *
     * @param context         the Android Context which initiated the request.
     * @param request         a raw {@link HttpUriRequest} to send, for example,
     *                        use this to {@link HttpGet}/{@link HttpPost} string/json/xml payloads
     *                        to a server(url) by passing a {@link org.apache.http.entity.StringEntity}.
     *                        and set headers(contentType) for this request
     * @param responseHandler the response handler instance that should handle
     *                        the response.
     */
    public void send(Context context, boolean retry, HttpUriRequest request, AsyncHttpResponseHandler responseHandler) {
        sendRequest(mHttpClient, retry, request, responseHandler, context);
    }


    protected void sendRequest(ManagedHttpClient client, boolean retry, HttpUriRequest uriRequest, String contentType, AsyncHttpResponseHandler responseHandler, Context context) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        sendRequest(client, retry, uriRequest, responseHandler, context);
    }

    protected void sendRequest(ManagedHttpClient client, boolean retry, HttpUriRequest uriRequest, AsyncHttpResponseHandler responseHandler, Context context) {
        Future<?> request = mThreadPool.submit(new AsyncHttpRequest(this, client, retry, uriRequest, responseHandler));
        if (context != null) {
            List<WeakReference<Future<?>>> requestList = mRequestMap.get(context);
            if (requestList == null) {
                requestList = new LinkedList<>();
                mRequestMap.put(context, requestList);
            }

            requestList.add(new WeakReference<Future<?>>(request));
        }
    }

    /**
     *
     */
    public String send(Context context, boolean retry, HttpUriRequest uriRequest) {

        syncResponse = null;
        /*
         * will execute the request directly
		 */
        new AsyncHttpRequest(this, mHttpClient, retry, uriRequest, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String content) {
                syncResponse = content;
                super.onSuccess(content);
            }

        }).run();

        return syncResponse;
    }

    public static String getUrlWithQueryString(String url, RequestParams params) {
        if (params != null) {
            String paramString = params.getParamString();
            if (!TextUtils.isEmpty(paramString)) {
                if (url.indexOf("?") == -1) {
                    url += "?" + paramString;
                } else {
                    url += "&" + paramString;
                }
            }
        }
        return url;
    }

    public ManagedHttpClient changeHttpClient(ManagedHttpClient client) {
        synchronized (mHttpClientLock) {
            if (client == null) {
                return mHttpClient;
            }

            if (client != mHttpClient) {
                return mHttpClient;
            }

            // 无网,不需要切换
            if (!RunningEnvironment.checkNetWork()) {
                return mHttpClient;
            }

            if (mSSLSocketFactory != null && mSSLSocketFactory instanceof SSLKeySetSocketFactory) {
                if (!((SSLKeySetSocketFactory) mSSLSocketFactory).hasSSLKey()) {
                    // 证书不正常,不需要重新创建
                    return mHttpClient;
                }
            }

            client.shutDown();

            // 维护证书环境
            if (mSSLSocketFactory != null && mSSLSocketFactory instanceof SSLKeySetSocketFactory) {
                ((SSLKeySetSocketFactory) mSSLSocketFactory).reset();
            }

            mHttpClient = createHttpClient(client, null);
            mRetryHandler.retryConnection(0);
            return mHttpClient;
        }
    }

    private HttpEntity paramsToEntity(RequestParams params) {
        HttpEntity entity = null;

        if (params != null) {
            entity = params.getEntity();
        }

        return entity;
    }

    private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
        if (entity != null) {
            requestBase.setEntity(entity);
        }

        return requestBase;
    }

    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}
