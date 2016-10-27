package com.yuelinghui.personal.maframe.util;

import android.annotation.SuppressLint;

import java.math.BigDecimal;

/**
 * 数字工具类
 */
public class DecimalUtil {

    /**
     * 金钱格式化
     *
     * @param number
     * @return
     */
    public static String format(BigDecimal number) {
        if (number == null) {
            return "0.00";
        }
        return number.setScale(2, BigDecimal.ROUND_DOWN).toString();
    }

    /**
     * 金钱格式化
     *
     * @param number
     * @return
     */
    public static String format(BigDecimal number, int scale) {
        if (number == null) {
            number = new BigDecimal("0");
        }
        return number.setScale(scale, BigDecimal.ROUND_DOWN).toString();
    }

    /**
     * 金钱格式化
     *
     * @param number
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String format(double number) {
        return String.format("%.2f", number);
    }

    /**
     * 金钱格式化，整数部分每三位加逗号
     *
     * @param number
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String formatAddComma(BigDecimal number) {
        if (number == null) {
            return "0.00";
        }
        String amount = number.setScale(2, BigDecimal.ROUND_DOWN).toString();
        StringBuilder local = new StringBuilder();
        String intPart = null;
        String[] strArray = amount.split("\\.");
        if (strArray != null && strArray.length > 0) {
            /** 格式化整数部分 **/
            try {
                if ("-0".equals(strArray[0])) {
                    intPart = String
                            .format("-%,d", Long.parseLong(strArray[0]));
                } else {
                    intPart = String.format("%,d", Long.parseLong(strArray[0]));
                }
            } catch (Exception e) {
                intPart = "0";
            }
            local.append(intPart);

            if (strArray.length > 1) {
                local.append(".").append(strArray[1]);
            }
        }
        return local.toString();
    }

    /**
     * 金钱格式化，整数部分每三位加逗号
     *
     * @param number
     * @param scale
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String formatAddComma(BigDecimal number, int scale) {
        if (number == null) {
            return BigDecimal.ZERO.setScale(scale, BigDecimal.ROUND_DOWN)
                    .toString();
        }
        String amount = number.setScale(scale, BigDecimal.ROUND_DOWN)
                .toString();
        StringBuilder local = new StringBuilder();
        String intPart = null;
        String[] strArray = amount.split("\\.");
        if (strArray != null && strArray.length > 0) {
            /** 格式化整数部分 **/
            try {
                if ("-0".equals(strArray[0])) {
                    intPart = String
                            .format("-%,d", Long.parseLong(strArray[0]));
                } else {
                    intPart = String.format("%,d", Long.parseLong(strArray[0]));
                }
            } catch (Exception e) {
                intPart = "0";
            }
            local.append(intPart);

            if (strArray.length > 1) {
                local.append(".").append(strArray[1]);
            }
        }
        return local.toString();
    }

    /**
     * 金钱格式化，整数部分每三位加逗号
     *
     * @param fen
     * @return
     */
    public static String formatAddComma(int fen) {
        return DecimalUtil.formatAddComma(DecimalUtil.toYuan(fen));
    }

    /**
     * 分转元
     *
     * @param fen 分
     * @return
     */
    public static BigDecimal toYuan(long fen) {
        return toYuan(fen, 2);
    }

    /**
     * 分转元
     *
     * @param fen 分
     * @return
     */
    public static BigDecimal toYuan(long fen, int scale) {
        if (scale < 0) {
            scale = 0;
        }
        return BigDecimal.valueOf(fen).setScale(scale, BigDecimal.ROUND_DOWN)
                .divide(new BigDecimal(100), BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal liToYuan(long li) {
        return BigDecimal.valueOf(li).setScale(3, BigDecimal.ROUND_DOWN)
                .divide(new BigDecimal(1000), BigDecimal.ROUND_DOWN);
    }

    /**
     * 元转分
     *
     * @param yuan 元
     * @return
     */
    public static long toFen(BigDecimal yuan) {
        if (yuan == null) {
            return 0;
        }
        return yuan.multiply(new BigDecimal(100))
                .setScale(0, BigDecimal.ROUND_DOWN).longValue();
    }

    /**
     * 元转厘
     *
     * @param yuan 元
     * @return
     */
    public static long toLi(BigDecimal yuan) {
        if (yuan == null) {
            return 0;
        }
        return yuan.multiply(new BigDecimal(1000))
                .setScale(0, BigDecimal.ROUND_DOWN).longValue();
    }

    /**
     * 元转分
     *
     * @param yuan 元
     * @return
     */
    public static String toFenStr(BigDecimal yuan) {
        if (yuan == null) {
            return "";
        }
        int amountFen = yuan.multiply(new BigDecimal(100))
                .setScale(0, BigDecimal.ROUND_DOWN).intValueExact();
        return String.valueOf(amountFen);
    }

    /**
     * 字符串解析为符合标准的字符串
     *
     * @param value 未格式化的字符串，小数点后两位的数字会截取
     * @return
     */
    public static BigDecimal parse(String value) {
        BigDecimal result = new BigDecimal(value);
        return result.setScale(2, BigDecimal.ROUND_DOWN);
    }

    /**
     * 字符串解析为符合标准的字符串
     *
     * @param value 未格式化的字符串，小数点后三位的数字会截取
     * @return
     */
    public static BigDecimal parseThreePoint(String value) {
        BigDecimal result = new BigDecimal(value);
        return result.setScale(3, BigDecimal.ROUND_DOWN);
    }

    /**
     * 判断是否小于0
     */
    public static boolean isNegative(BigDecimal number) {
        if (number == null) {
            return false;
        }
        return number.signum() == -1;
    }

    /**
     * 判断是否等于0
     */
    public static boolean isZero(BigDecimal number) {
        if (number == null) {
            return false;
        }
        return number.signum() == 0;
    }

    /**
     * 判断是否大于0
     *
     * @param number
     * @return
     */
    public static boolean isPositive(BigDecimal number) {
        if (number == null) {
            return false;
        }
        return number.signum() == 1;
    }
}
