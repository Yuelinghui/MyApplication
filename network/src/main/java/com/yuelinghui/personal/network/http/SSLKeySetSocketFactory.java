package com.yuelinghui.personal.network.http;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by yuelinghui on 16/9/26.
 */

public class SSLKeySetSocketFactory extends SSLSocketFactory {

    private SSLContext mSSLContext = SSLContext.getInstance("TLS");

    private ArrayList<byte[]> mKeys = new ArrayList<>(5);

    public SSLKeySetSocketFactory(byte[] publicKey)
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {

        super(null);
        initSSL();
        addSSLKey(publicKey);
    }

    /**
     * 初始化SSL
     */
    private void initSSL() {
        TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {

                if (mKeys == null || mKeys.size() == 0) {
                    throw new CertificateException("checkServerTrusted:p ublicKey is null");
                }

                assert (chain != null);
                if (chain == null) {
                    throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
                }

                assert (chain.length > 0);
                if (chain.length <= 0) {
                    throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
                }

                boolean expected = false;
                byte[] key = chain[0].getPublicKey().getEncoded();
                for (byte[] k : mKeys) {
                    if (Arrays.equals(k,key)) {
                        expected = true;
                        break;
                    }
                }

                if (!expected) {
                    throw new CertificateException("checkServerTrusted: Not expected public key");
                }

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        try {
            mSSLContext.init(null,new TrustManager[]{tm},null);
        } catch (KeyManagementException e) {

        }
    }

    /**
     * 重置SSL环境,请调用关闭连接(ThreadClientManager.shutDown()),确保所有连接已经关闭
     */
    public void reset() {
        SSLSessionContext context = mSSLContext.getClientSessionContext();
        if (context != null) {
            try {
                Enumeration<byte[]> sessionIds = context.getIds();
                if (sessionIds != null) {
                    byte[] sessionId = null;
                    SSLSession session = null;
                    while (sessionIds.hasMoreElements()) {
                        sessionId = sessionIds.nextElement();
                        if (sessionId != null) {
                            session = context.getSession(sessionId);
                            if (session != null) {
                                session.invalidate();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return mSSLContext.getSocketFactory().createSocket(socket,host,port,autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return mSSLContext.getSocketFactory().createSocket();
    }

    /**
     * 是否包含正确的key
     * @return
     */
    public boolean hasSSLKey() {
        return mKeys != null && mKeys.size() > 0;
    }

    /**
     * 添加SSL相关key到队列
     * @param key
     */
    public void addSSLKey(byte[] key) {
        if (key == null) {
            return;
        }
        if (mKeys == null) {
            mKeys = new ArrayList<>(5);
        }
        for (byte[] k: mKeys) {
            if (Arrays.equals(k,key)) {
                return;
            }
        }
        mKeys.add(key);
    }
}
