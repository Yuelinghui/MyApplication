package com.yuelinghui.personal.maframe.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * 参数校验类
 *
 * @author xingtongju qiuchunlong
 */
public class CheckUtil {

    /**
     * 验证邮箱地址是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        String pattern = "[_\\w\\-]+(\\.[_\\w\\-]*)*@[\\w\\-]+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.compile(pattern).matcher(email).matches();
    }

    /**
     * 校验金额大小
     *
     * @param amount
     * @return
     * @author wyqiuchunlong
     */
    public static boolean isAmount(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("0.01")) < 1
                || amount.compareTo(new BigDecimal("99999999999999.99")) >= 0) {
            return false;
        }
        return true;
    }

    /**
     * 验证手机号码
     *
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return false;
        }
        Pattern phonner = Pattern.compile("[0-9]{11}");
        return phonner.matcher(mobile).matches();
    }

    /**
     * 校验是否符合账户名
     *
     * @param accout
     * @return
     */
    public static boolean isAccount(String accout) {
        if (isMobile(accout) || isEmail(accout)) {
            return true;
        }
        return false;
    }

    /**
     * 验证身份证
     *
     * @param text 身份证号码
     * @return true：合法， false：不合法
     * @author xingtongju
     */
    public static boolean isID(String ID) {
        if (ID == null || ID.trim().length() == 0)
            return false;
        String regx1 = "[0-9]{17}x";
        String regx2 = "[0-9]{17}X";
        String regx3 = "[0-9]{15}";
        String rege4 = "[0-9]{18}";
        return (ID.matches(regx1) || ID.matches(regx2) || ID.matches(regx3) || ID
                .matches(rege4));
    }

    /**
     * 校验银行卡卡号
     *
     * @param bankCard
     * @return
     */
    public static boolean isBankCard(String bankCard) {
        if (TextUtils.isEmpty(bankCard))
            return false;
        String cardId = bankCard.trim();
        String reg1 = "[0-9]{14,19}";
        if (cardId.matches(reg1)) {
            return true;
        }
        return false;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * 算法比较简单。
     * 1、除去校验位后，从右至左，将卡号按位编码，从0开始。
     * 2、将偶数位×2，得到的结果按位相加，比如偶数为6，×2＝12，则将1和2相加＝3；奇数位则直接参与相加；
     * 3、重复步骤2得到总和，该总和加上校验位应能被10整除，否则校验位不正确。
     * 图解：
     * 设卡号：
     * 1    3    8     6    2    6     7    1     8     check
     * ×2  ＝ 2        16           4          14        16
     * --------------------------------------------------------------
     * 2 +3+1+6+6+4+6+1+4+1+1+6 = x
     * (( x + check )%10 == 0 )
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 检测短信验证码格式
     *
     * @param checkcode
     * @return 合法：true
     */
    public static boolean isSMSCheckCode(String checkcode) {
        if (TextUtils.isEmpty(checkcode)) {
            return false;
        }
        String regex = "[A-Za-z0-9]{6}";
        return (checkcode.matches(regex));
    }

    /**
     * 校验密码格式
     *
     * @param password
     * @return
     */
    public static boolean isPassword(String password) {
        return isPassword(password, false);
    }

    /**
     * 校验密码格式
     *
     * @param password
     * @param strict   严格校验
     * @return
     */
    public static boolean isPassword(String password, boolean strict) {
        if (password == null) {
            return false;
        }
        password = password.trim();

        boolean result = false;

        try {
            // 位数和非空
            String regLen = "^.{6,20}$";
            String regSpace = "[\\s]";
            result = Pattern.compile(regLen).matcher(password).find()
                    && !Pattern.compile(regSpace).matcher(password).find();
            if (!result) {
                return false;
            }
            if (!strict) {
                return result;
            }

            // 严格限定
            int matchCount = 0;
            // 数字
            String reg1 = "[0-9]";
            if (Pattern.compile(reg1).matcher(password).find()) {
                matchCount++;
            }
            // 小写字母
            String reg2 = "[a-z]";
            if (Pattern.compile(reg2).matcher(password).find()) {
                matchCount++;
            }
            // 大写字母
            String reg3 = "[A-Z]";
            if (Pattern.compile(reg3).matcher(password).find()) {
                matchCount++;
            }
            // 字符
            String reg4 = "[^0-9a-zA-Z]";
            if (Pattern.compile(reg4).matcher(password).find()) {
                matchCount++;
            }

            result = matchCount >= 2;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * 判断是否是cvv
     *
     * @param cvv
     * @return
     * @author wyqiuchunlong
     */
    public static boolean isCVV(String cvv) {
        if (TextUtils.isEmpty(cvv) || !isNumeric(cvv) || cvv.length() != 3) {
            return false;
        }
        return true;
    }

    /**
     * 校验信用卡有效期
     *
     * @param password
     * @return
     */
    public static boolean isValidDate(String validDate) {
        if (validDate == null) {
            return false;
        }
        validDate = validDate.trim();
        // 数字
        String reg1 = "(0[1-9]|1[0-2])/[0-9]{2}";
        return Pattern.compile(reg1).matcher(validDate).find();
    }

    /**
     * 验证URL地址
     *
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或
     *            http://www.csdn.net:80
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isURL(String url) {
        String http = "http://";
        String https = "https://";
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith(http) || url.startsWith(https);
    }


    /**
     * 是否为汉字
     *
     * @param word
     * @return
     * @author wyqiuchunlong
     */
    public static boolean isChinese(char c) {
        if ((int) c == 65292) {
            return false;
        }
        if ((19968 <= (int) c) && ((int) c <= 171941)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为英文
     *
     * @param c
     * @return
     * @author wyqiuchunlong
     */
    public static boolean isABC(char c) {
        if (!(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z')) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return
     * @author wyqiuchunlong
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否为表情
     *
     * @param str
     * @return
     * @author wyxionglili
     */
    public static boolean isFace(char c) {
        if ((int) c == 55357) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为实名，实名需中文或英文，2-18位
     *
     * @param name
     * @return
     * @author wyqiuchunlong
     */
    public static boolean isName(String name) {
        int length = name.length();
        if (length < 2 || length > 18) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为实名，实名需中文或英文，2-18位
     *
     * @param name
     * @return
     * @author wyqiuchunlong
     */
    public static boolean isName(char c) {
        if (isChinese(c) || isABC(c) || c == ' ' || c == '.' || c == '*') {
            return true;
        }
        return false;
    }
}
