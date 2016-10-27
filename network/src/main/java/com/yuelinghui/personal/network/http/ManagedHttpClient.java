package com.yuelinghui.personal.network.http;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

/**
 * Created by yuelinghui on 16/9/26.
 */

public class ManagedHttpClient extends DefaultHttpClient {
    /**
     * httpClient对应的上下文
     */
    public HttpContext httpContext = new SyncBasicHttpContext(new BasicHttpContext());
    /**
     * client是否关闭
     */
    public volatile boolean isShutDown;

    public ManagedHttpClient() {
        super();
    }

    public ManagedHttpClient(ClientConnectionManager manager, HttpParams params) {
        super(manager,params);
    }

    public ManagedHttpClient(HttpParams params) {
        super(params);
    }

    /**
     * 关闭httpClient,释放资源等
     */
    public void shutDown() {
        isShutDown = true;

        try {
            getConnectionManager().shutdown();
        }catch (Exception e) {
        }
    }

}
