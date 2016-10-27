package com.yuelinghui.personal.network.mock;

import android.content.Context;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by yuelinghui on 16/9/27.
 */

public class MockSSLSocketFactory extends SSLSocketFactory {


    SSLContext sslContext = SSLContext.getInstance("TLS");

    public MockSSLSocketFactory(Context context)
            throws KeyManagementException, UnrecoverableKeyException,
            NoSuchAlgorithmException, KeyStoreException, IOException,
            CertificateException, NoSuchProviderException {
        super(createOnlinePayKeyStore(context));

        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sslContext.init(null, new TrustManager[] { tm }, null);
        setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);;
    }

    private static KeyStore createOnlinePayKeyStore(Context context)
            throws IOException, CertificateException, KeyStoreException,
            NoSuchProviderException, NoSuchAlgorithmException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        return trustStore;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port,
                autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }


}
