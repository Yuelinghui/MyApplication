package com.yuelinghui.personal.maframe.util.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {
    public static String genMD5(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // e.printStackTrace();
            return "";
        }
        String str = input;
        md.update(str.getBytes());
        byte[] encodedPassword = md.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encodedPassword.length; i++) {
            if ((encodedPassword[i] & 0xff) < 0x10) {
                sb.append("0");
            }

            sb.append(Long.toString(encodedPassword[i] & 0xff, 16));
        }
        return sb.toString();
    }
}
