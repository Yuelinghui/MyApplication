package com.yuelinghui.personal.network.util;

import android.text.TextUtils;

import com.yuelinghui.personal.network.protocol.RequestParam;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuelinghui on 16/9/27.
 */

public class UrlParser {

        /**
         * 解析出url请求的路径，包括页面
         *
         * @param strURL
         *            url地址
         * @return url路径
         */
        public static String parseAction(String strURL) {
            String strPage = null;
            String[] arrSplit = null;
            if (!TextUtils.isEmpty(strURL)) {
                strURL = strURL.trim();
                arrSplit = strURL.split("[?]");
                if (arrSplit.length > 0) {
                    strPage = arrSplit[0];
                }
            }
            return strPage;
        }

        /**
         * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
         *
         * @param URL
         *            url地址
         * @return url请求参数部分
         */
        public static Map<String, String> parseParam(String URL) {
            if (TextUtils.isEmpty(URL)) {
                return null;
            }
            String strUrlParam = TruncateUrlPage(URL);
            if (TextUtils.isEmpty(strUrlParam)) {
                return null;
            }

            Map<String, String> mapRequest = new HashMap<String, String>();
            // 每个键值为一组
            String[] arrSplit = strUrlParam.split("[&]");
            for (String strSplit : arrSplit) {
                String[] arrSplitEqual = null;
                arrSplitEqual = strSplit.split("[=]");

                // 解析出键值
                if (arrSplitEqual.length > 1) {
                    // 正确解析
                    mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
                } else {
                    if (arrSplitEqual[0] != "") {
                        // 只有参数没有值，不加入
                        mapRequest.put(arrSplitEqual[0], "");
                    }
                }
            }
            return mapRequest;
        }

        /**
         * 去掉url中的路径，留下请求参数部分
         *
         * @param strURL
         *            url地址
         * @return url请求参数部分
         */
        private static String TruncateUrlPage(String strURL) {
            if (TextUtils.isEmpty(strURL)) {
                return null;
            }
            String strAllParam = null;
            String[] arrSplit = null;
            strURL = strURL.trim();
            arrSplit = strURL.split("[?]");
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
            return strAllParam;
        }

        /**
         * 对get请求参数encoder
         *
         * @param param
         * @return
         */
        public static String getURLEncoderParams(RequestParam param) {
            StringBuilder strData = new StringBuilder();
            // 遍历父类属性
            for (Class<?> cls = param.getClass(); cls != Object.class; cls = cls
                    .getSuperclass()) {
                Field[] fields = cls.getDeclaredFields();
                for (Field field : fields) {
                    try {
                        // 获取原来的访问控制权限
                        boolean accessFlag = field.isAccessible();
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        Object fieldValueObj = field.get(param);
                        String fieldValue = null;
                        fieldValue = String.valueOf(fieldValueObj);
                        field.setAccessible(accessFlag);
                        // 添加数据
                        strData.append(fieldName)
                                .append("=")
                                .append(TextUtils.isEmpty(fieldValue) ? ""
                                        : URLEncoder.encode(fieldValue, "UTF-8"))
                                .append("&");
                    } catch (Exception e) {
                    }
                }
            }
            // 移除最后一个&符号
            if (strData.length() > 0) {
                strData.delete(strData.length() - 1, strData.length());
            }
            return strData.toString();
        }

}
