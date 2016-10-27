package com.yuelinghui.personal.maframe.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * 安全工具辅助类
 */
public class CertificateUtil {

    private static final String CERTIFICATE_TYPE = "X.509";

    /**
     * 创建包含证书的KeyStore
     *
     * @param cerStream 证书的输入流
     * @return
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static KeyStore createCertificateKeyStore(InputStream cerStream)
            throws CertificateException, KeyStoreException,
            NoSuchProviderException, NoSuchAlgorithmException, IOException {
        CertificateFactory cerFactory = CertificateFactory
                .getInstance(CERTIFICATE_TYPE);
        Certificate cer = cerFactory.generateCertificate(cerStream);
        KeyStore trustStore = KeyStore.getInstance("PKCS12", "BC");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("trust", cer);

        return trustStore;
    }

    /**
     * 获取证书中的公钥
     *
     * @param cerStream
     * @return
     * @throws CertificateException
     */
    public static byte[] getCertificatePublicKey(InputStream cerStream)
            throws CertificateException {
        CertificateFactory cerFactory = CertificateFactory
                .getInstance(CERTIFICATE_TYPE);
        Certificate cer = cerFactory.generateCertificate(cerStream);
        return cer.getPublicKey().getEncoded();
    }
}
