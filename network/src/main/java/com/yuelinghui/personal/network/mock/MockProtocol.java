package com.yuelinghui.personal.network.mock;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.yuelinghui.personal.network.protocol.RequestParam;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by yuelinghui on 16/9/27.
 */

public abstract class MockProtocol {


    /**
     * Mock下的短信验证码
     */
    public static final String CHECKCODE = "111111";

    /**
     * 模拟执行请求，返回响应内容
     *
     * @param param
     * @return
     */
    public abstract String execute(RequestParam param);

    /**
     * 预期结果表明构建什么样的结果字符串
     *
     * @param param
     * @return
     */
    public int expectResult(RequestParam param) {
        int result = 0;
        try {
            result = MockConfig.getMockConfigResult(param);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 构造结果字符串
     */
    public String resultContent(int resultCode, String resultMsg,
                                Object resultContentObj) {
        return String.format(
                "{\"resultData\":%s,\"resultMsg\":\"%s\",\"resultCode\":%d}",
                toJson(resultContentObj), resultMsg == null ? "" : resultMsg,
                resultCode);
    }

    /**
     * 构造结果字符串
     */
    public String resultContent(int resultCode, String resultMsg,
                                Object resultContentObj, Object controlObj) {
        return String
                .format("{\"resultCtrl\":%s,\"resultData\":%s,\"resultMsg\":\"%s\",\"resultCode\":%d}",
                        toJson(controlObj), toJson(resultContentObj),
                        resultMsg == null ? "" : resultMsg, resultCode);
    }

    /**
     * 校验验证码
     *
     * @param code
     * @return
     */
    public boolean verifyActiveCode(String code) {
        return CHECKCODE.equals(code);
    }

    private Gson gson = new Gson();

    /**
     * 对象转json
     *
     * @param obj
     * @return
     */
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 工具类函数
     */
    private Random r = new Random(100);

    protected BigDecimal random4Decimal(int maxInt) {
        return new BigDecimal(String.format("%d.%04d", r.nextInt(maxInt),
                r.nextInt(9999)));
    }

    protected BigDecimal random2Decimal(int maxInt) {
        return new BigDecimal(String.format("%d.%02d", r.nextInt(maxInt),
                r.nextInt(99)));
    }

    protected String randomTime() {
        return String.format("%04d-%02d-%02d 00:00:00", r.nextInt(20) + 2000,
                r.nextInt(12) + 1, r.nextInt(31) + 1);
    }

    @SuppressLint("DefaultLocale")
    protected String randomString(String prefix, int maxInt) {
        return String.format("%s%5d", prefix, r.nextInt(maxInt));
    }

    protected int randomInt(int maxInt) {
        return r.nextInt(maxInt);
    }

}
