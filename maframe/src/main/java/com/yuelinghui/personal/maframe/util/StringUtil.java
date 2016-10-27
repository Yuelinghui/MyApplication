package com.yuelinghui.personal.maframe.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class StringUtil {


    static final byte[] HEX_CHAR_TABLE = {(byte) '0', (byte) '1', (byte) '2',
            (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
            (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c',
            (byte) 'd', (byte) 'e', (byte) 'f'};
    private static final String maskString = "****";
    private static final String mBlankString = "  ";
    private static final char maskChar = '*';
    private static final int ACCOUNT_DISPLAY_LENGTH = 20;

    public static String getHexString(byte[] raw) {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }

        String result = "";
        try {
            result = new String(hex, "ASCII");
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    /**
     * @param input
     * @return
     */
    static public String trim(String input) {
        if (input != null && input.length() > 0) {
            String empty = String.valueOf("　");
            String cr = String.valueOf("\r");
            String ln = String.valueOf("\n");
            input = input.trim();
            while (input.startsWith(empty) || input.startsWith(cr)
                    || input.startsWith(ln)) {
                input = input.substring(1, input.length()).trim();
            }
            while (input.endsWith(empty) || input.endsWith(cr)
                    || input.endsWith(ln)) {
                input = input.substring(0, input.length() - 1).trim();
            }
        }

        return input;
    }

    /**
     * 获取手机号
     *
     * @param number
     * @return
     * @author wyqiuchunlong
     */
    public static String getMobile(String number) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }

        // 去掉非数字
        number = number.replaceAll("\\D+", "").trim();
        int length = number.length();
        if (length < 11) {
            return null;
        }

        return number.substring(length - 11, length);
    }

    /**
     * 获取电话的格式化字符串 ， 如：135 6666 8888
     *
     * @param mobile
     * @return
     */
    public static String getPhoneFormat(String mobile) {
        if (CheckUtil.isMobile(mobile)) {
            StringBuffer buffer = new StringBuffer(mobile);
            buffer.insert(3, ' ');
            buffer.insert(8, ' ');
            return buffer.toString();
        }
        return mobile;
    }

    /**
     * 去掉字符串间所有的字符
     *
     * @param value 原字符串
     * @param str   要去掉的字符
     * @return
     * @author wyqiuchunlong
     */
    public static String replaceAllStr(String value, String str) {
        return value.replaceAll(str, "").trim();
    }

    /**
     * 隐藏手机号码中间四位
     *
     * @param phone
     * @return
     */
    public static String maskPhone(String phone) {
        if (CheckUtil.isMobile(phone)) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(phone.substring(0, 3));
            buffer.append(maskString);
            buffer.append(phone.substring(7, 11));
            return buffer.toString();
        }
        return phone;
    }

    /**
     * 获取隐藏后的邮箱
     */
    public static String maskEmail(String email) {

        if (TextUtils.isEmpty(email) || email.length() <= 3) {
            return email;
        }

        StringBuilder sb = new StringBuilder(email);
        sb.delete(0, 3);
        sb.insert(0, maskChar);
        sb.insert(0, maskChar);
        sb.insert(0, maskChar);
        return sb.toString();
    }

    /**
     * 获取隐藏后的账号名
     *
     * @param account
     * @return
     */
    public static String maskAccount(String account) {
        if (CheckUtil.isMobile(account)) {
            return maskPhone(account);
        }
        if (CheckUtil.isEmail(account)) {
            return maskEmail(account);
        }
        return account;
    }

    /**
     * 隐藏银行卡号
     *
     * @param bankCard
     * @return
     */
    public static String getMaskbankCard(String bankCard) {

        if (!CheckUtil.isBankCard(bankCard)) {
            return bankCard;
        }

        int index = 0;
        int length = bankCard.length();
        int duan = (length - 4) / 4;
        int yu = (length - 4) % 4;

        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < duan; i++) {
            buffer.append(maskString);
            buffer.append(mBlankString);
            index += 4;
        }

        for (int i = 0; i < yu; i++) {
            buffer.append(maskChar);
            index++;
        }

        buffer.append(bankCard, index, index + (4 - yu));
        index += 4 - yu;

        buffer.append(mBlankString);
        buffer.append(bankCard, index, length);

        return buffer.toString();
    }

    /**
     * 格式化卡号 如1234 5678 1234 5678
     *
     * @return
     */
    public static String formatCardNum(String cardNum) {
        if (!CheckUtil.isBankCard(cardNum)) {
            return cardNum;
        }
        StringBuilder formated = new StringBuilder();

        int length = cardNum.length();
        for (int i = 0; i < length; i++) {
            char c = cardNum.charAt(i);
            formated.append(c);
            if ((i + 1) % 4 == 0) {
                formated.append(" ");
            }
        }

        return formated.toString().trim();
    }

    /**
     * 获取银行卡号后四位
     *
     * @param bankCard
     * @return
     */
    public static String getBankCardTail4(String bankCard) {
        if (bankCard != null && bankCard.length() > 4) {
            return bankCard.substring(bankCard.length() - 4, bankCard.length());
        }
        return bankCard;
    }

    /**
     * 获取截断的账户名
     *
     * @param account
     * @return
     */
    public static String getCutAccount(String account) {
        if (!TextUtils.isEmpty(account)
                && account.length() > ACCOUNT_DISPLAY_LENGTH) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(account.substring(0, ACCOUNT_DISPLAY_LENGTH - 1));
            buffer.append("...");
            return buffer.toString();
        }
        return account;
    }

    /**
     * 获取短信验证码
     *
     * @param message
     * @return
     * @author wyqiuchunlong
     */
    public static String getCheckCode(String checkCode) {
        Pattern p = Pattern.compile("(?<!\\d)(\\d{6})(?!\\d)");
        Matcher m = p.matcher(checkCode);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    /**
     * 截取精确到时分秒的data字符串前X位
     */
    public static String getFirstSubStrings(String origin, int firstNums) {
        if (origin == null) {
            return "";
        }
        if (origin.length() < firstNums) {
            firstNums = origin.length();
        }

        return origin.substring(0, firstNums);
    }

    /**
     * 将手机号中的区号+86去掉
     *
     * @param mobile
     * @return
     */
    public static String removeAreaCodeFromPhone(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return mobile;
        }
        return mobile.replace("+86", "");
    }

    /**
     * 删除手机号中的区号，删除后如果手机号位数应该11位
     *
     * @param mobile
     * @return null表示手机号不合法，有string返回表示删除了区号的合法手机号
     */
    public static String convertToValidPhoneNumber(String mobile) {
        String phoneNumber = removeAreaCodeFromPhone(mobile);
        if (phoneNumber != null && phoneNumber.length() == 11) {
            return phoneNumber;
        }
        return null;
    }

    /**
     * 去掉错有非数
     *
     * @param charSequence
     * @return
     */
    public static String replaceNonNumber(String charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(charSequence);
        charSequence = m.replaceAll("");
        return charSequence;
    }

    /**
     * 判断两个string是否相同，包括判空
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean compareString(String str1, String str2) {
        if (TextUtils.isEmpty(str1) && !TextUtils.isEmpty(str2)) {
            return false;
        } else if (!TextUtils.isEmpty(str1) && TextUtils.isEmpty(str2)) {
            return false;
        } else if (!TextUtils.isEmpty(str1) && !TextUtils.isEmpty(str2)
                && !str1.equals(str2)) {
            return false;
        }
        return true;
    }

}
