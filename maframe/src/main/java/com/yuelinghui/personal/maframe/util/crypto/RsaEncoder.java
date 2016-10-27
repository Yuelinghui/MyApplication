package com.yuelinghui.personal.maframe.util.crypto;

import android.text.TextUtils;

import com.yuelinghui.personal.maframe.util.Base64;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

/**
 * @author qt-liuguanqing
 *         Rsa加密辅助类
 */
public class RsaEncoder {

    private final static int PT_LEN = 117;// 1024位RSA加密算法中,当加密明文长度超过117个字节后,会出现异常,所以采用分段加密
    private final static String SPRIT_CHAR = "|";//分段加密/解密,段落分割符
    private PublicKey rsaPublicKey;
    private Cipher cipher;

    private RsaEncoder(String publicKey) throws Exception {
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        this.rsaPublicKey = this.generatePublic(publicKey);
    }

    private RsaEncoder(byte[] publicKey) throws Exception {
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        this.rsaPublicKey = this.generatePublic(publicKey);
    }

    /**
     * 根据公钥,获取加密工具类实例
     *
     * @param publicKey
     * @return
     */
    public static RsaEncoder getInstance(String publicKey) throws Exception {
        if (TextUtils.isEmpty(publicKey)) {
            return null;
        }
        return new RsaEncoder(publicKey);
    }

    public static RsaEncoder getInstance(byte[] publicKey) throws Exception {
        return new RsaEncoder(publicKey);
    }

    /**
     * 字符错位算法
     *
     * @param ori
     * @return
     */
    public static byte[] confuse(byte[] ori) {
        for (int i = 0, byteLength = ori.length; i < byteLength; i++) {
            ori[i] = (byte) ~ori[i];
        }
        return ori;

    }

    private PublicKey generatePublic(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decode(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 生成公钥对象
     *
     * @param keyBytes
     * @return
     * @throws Exception
     * @author liuzhiyun
     */
    private PublicKey generatePublic(byte[] keyBytes) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 加密输入的明文字符串
     * 当value的字节长度大于117,将会采用分段加密,即依次对117个字节,加密,并通过"|"对段落进行分割,请解密者注意
     *
     * @param value 加密后的字符串 1024个字节长度
     * @return
     */
    public String encrypt(String value) throws IOException {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        return encryptBySeg(value.getBytes("utf-8"), Base64.NO_OPTIONS);
    }

    /**
     * 分段加密
     *
     * @param plainText,各个段落以'|'分割
     * @param option
     * @return
     * @throws IOException
     */
    private String encryptBySeg(byte[] plainText, int option) throws IOException {
        //获取加密段落个数
        int length = plainText.length;//
        int mod = length % PT_LEN;//余数
        int ptime = length / PT_LEN;//段数
        int ptimes = (mod == 0 ? ptime : ptime + 1);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < ptimes) {
            int from = i * PT_LEN;
            int to = Math.min(length, (i + 1) * PT_LEN);
            byte[] temp = Arrays.copyOfRange(plainText, from, to);
            sb.append(Base64.encodeBytes(encrypt(temp), option));
            if (i != (ptimes - 1)) {
                sb.append(SPRIT_CHAR);
            }
            i++;
        }
        return sb.toString();

    }

    /**
     * 加密
     *
     * @param plainTextArray
     * @return
     */
    private byte[] encrypt(byte[] plainTextArray) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            byte[] encryptByteArray = cipher.doFinal(plainTextArray);
            return encryptByteArray;
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * 重置加密key
     *
     * @param publicKey
     */
    public void reset(String publicKey) throws Exception {
        this.rsaPublicKey = this.generatePublic(publicKey);
    }

    /**
     * 重置加密key
     *
     * @param publicKey
     * @author liuzhiyun
     */
    public void reset(byte[] publicKey) throws Exception {
        this.rsaPublicKey = this.generatePublic(publicKey);
    }
}
