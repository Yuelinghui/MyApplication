package com.yuelinghui.personal.maframe.util.crypto;

import android.text.TextUtils;

import com.yuelinghui.personal.maframe.util.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.StringTokenizer;

import javax.crypto.Cipher;

/**
 * @author qt-liuguanqing
 *         Rsa解密辅助类
 */
public class RsaDecoder {

    private final static String SPRIT_CHAR = "|";//分段加密/解密,段落分割符
    private PrivateKey rsaPrivateKey;
    private Cipher cipher;

    private RsaDecoder(String privateKey) throws Exception {
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        this.rsaPrivateKey = this.generatePrivate(privateKey);
    }

    private RsaDecoder(byte[] privateKey) throws Exception {
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        this.rsaPrivateKey = this.generatePrivate(privateKey);
    }

    /**
     * 根据私钥获取解密对象
     *
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static RsaDecoder getInstance(String privateKey) throws Exception {
        if (TextUtils.isEmpty(privateKey)) {
            return null;
        }
        return new RsaDecoder(privateKey);
    }

    /**
     * 根据私钥获取解密对象
     *
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static RsaDecoder getInstance(byte[] privateKey) throws Exception {
        return new RsaDecoder(privateKey);
    }

    /**
     * 获取私钥,根据已经生成的合格的RSA私钥字符串,转化成RSAPrivateKey对象,(PKCS8EncodedKeySpec)
     *
     * @param key
     * @return
     * @throws Exception
     */
    private PrivateKey generatePrivate(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decode(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 获取私钥,根据已经生成的合格的RSA私钥字符串,转化成RSAPrivateKey对象,(PKCS8EncodedKeySpec)
     *
     * @return
     * @throws Exception
     */
    private PrivateKey generatePrivate(byte[] keyBytes) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 解密
     *
     * @param encrypt 加密后的二进制字节
     * @return 解密后的二进制
     */
    private byte[] dencrypt(byte[] encrypt) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            byte[] decryptByteArray = cipher.doFinal(encrypt);
            return decryptByteArray;
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * 对加密数据进行解密,自动分段
     *
     */
    public String dencrypt(String plainTextA) {
        StringTokenizer tokenizer = new StringTokenizer(plainTextA, SPRIT_CHAR);
        StringBuffer sb = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            byte[] tmp;
            String tmpBase64Str = (String) tokenizer.nextElement();
            try {
                // firefox_comment it is formatted when 1024key, but not 2048, so ignore it.
                tmp = Base64.decode(tmpBase64Str);
                tmp = dencrypt(tmp);
                sb.append(new String(tmp, "utf-8"));
            } catch (Exception e) {
            }
        }
        //替换空格
        return sb.toString().replace("\u0000", "");

    }

    /**
     * @param str
     * @return
     */
    public String getFromatBase64String(String str, int times) {
        int timesModes = (int) (Math.pow(1.5, times - 1) * 10);
        final int subLength = 172 * timesModes / 10;//这个数字是由RSA1024位　及base 64增大0.5倍，具体计算公式为：rsa密钥长度/8*(1.5);
        String ret = str.substring(str.length() - subLength, str.length());
        return ret;

    }

    /**
     * 重置加密key
     *
     */
    public void reset(String privateKey) throws Exception {
        this.rsaPrivateKey = this.generatePrivate(privateKey);
    }

    /**
     * 重置加密key
     *
     */
    public void reset(byte[] privateKey) throws Exception {
        this.rsaPrivateKey = this.generatePrivate(privateKey);
    }
}
