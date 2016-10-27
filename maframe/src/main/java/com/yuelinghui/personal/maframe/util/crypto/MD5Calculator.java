package com.yuelinghui.personal.maframe.util.crypto;

import com.yuelinghui.personal.maframe.util.StringUtil;

import java.security.MessageDigest;

public class MD5Calculator {
    private static MessageDigest _digest;

    public static String calculateMD5(String s) {
        try {
            return calculateMD5(s.getBytes("UTF-8"));
        } catch (Exception e) {
        }
        return "";
    }

    public synchronized static String calculateMD5(byte[] input) {
        try {
            _digest = MessageDigest.getInstance("MD5");
            _digest.reset();
            _digest.update(input);
            byte[] hash = _digest.digest();
            return StringUtil.getHexString(hash);
        } catch (Exception e) {

        }
        return "";
    }
}
