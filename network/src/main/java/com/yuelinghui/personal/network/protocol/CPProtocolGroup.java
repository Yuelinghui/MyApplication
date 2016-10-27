package com.yuelinghui.personal.network.protocol;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yuelinghui.personal.maframe.are.RunningEnvironment;
import com.yuelinghui.personal.maframe.result.TypedResult;
import com.yuelinghui.personal.network.http.AsyncHttpClient;
import com.yuelinghui.personal.network.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by yuelinghui on 16/9/27.
 */
public class CPProtocolGroup {
    /**
     * JSON 节点名称
     */
//    private static final String FIELD_RESULT_CODE = "resultCode";
//    private static final String FIELD_RESULT_CONTROL = "resultCtrl";
//    private static final String FIELD_RESULT_MESSAGE = "resultMsg";
//    private static final String FIELD_RESULT_DATA = "resultData";

    /**
     * HTTP Header名称
     */
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

    private static HashMap<Class<?>, CPProtocolAction> mActions = new HashMap<>(20);

    public static CPProtocolAction addAction(Class<? extends RequestParam> param, CPProtocolAction action) {
        return mActions.put(param, action);
    }

    public static CPProtocolAction getAction(RequestParam param) {
        return mActions.get(param.getClass());
    }

    public static CPProtocolAction getAction(Class<? extends RequestParam> paramClass) {
        return mActions.get(paramClass);
    }

    /**
     * 获取指定协议的请求参数列表
     *
     * @param protocol
     * @return
     */
    public static List<Class<?>> getProtocolRequestParams(CPProtocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("protocol must not be null");
        }

        List<Class<?>> params = new ArrayList<>(5);
        Class<?> key = null;

        String protocolPkg = protocol.getClass().getPackage().getName();
        for (Map.Entry<Class<?>, CPProtocolAction> entry : mActions.entrySet()) {
            key = entry.getKey();
            if (protocolPkg.equals(key.getPackage().getName())) {
                params.add(key);
            }
        }
        return params;
    }

    /**
     * 封装请求参数，创建可执行请求
     *
     * @param param
     * @return
     * @throws UnsupportedEncodingException
     */
    public Request buildPostRequest(RequestParam param) throws UnsupportedEncodingException {
        CPProtocolAction action = getAction(param);
        if (action == null) {
            throw new IllegalArgumentException("action not found:" + param.getClass());
        }
        return new Request(buildPostRequest(action, param), action.retry);
    }

    public Request buildGetRequest(RequestParam requestParam, UrlParam urlParam) throws UnsupportedEncodingException {
        CPProtocolAction action = null;
        if (requestParam != null) {
             action = getAction(requestParam);
        }
        if (action == null && urlParam != null) {
            action = getAction(urlParam);
        }
        if (action == null) {
            throw new IllegalArgumentException("action not found:" + requestParam.getClass());
        }
        return new Request(buildGetRequest(action.url, requestParam, urlParam), action.retry);
    }

    /**
     * 创建Get请求对象
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    protected HttpUriRequest buildGetRequest(String url, RequestParam requestParam, UrlParam urlParam)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(url);
        if (urlParam != null) {
            Class urlParamCls = urlParam.getClass();
            Field[] fs = urlParamCls.getDeclaredFields();
            if (fs != null && fs.length > 0) {
                for (Field f : fs) {
                    f.setAccessible(true);
                    String key = f.getName();
                    if (key.startsWith("$") || key.startsWith("serialVersionUID")) {
                        continue;
                    }
                    String value = null;
                    try {
                        value = f.get(urlParam).toString();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    sb.append("/");
                    sb.append(value);
                }
            }
        }
        url = sb.toString();
        if (requestParam != null) {
            Class paramCls = requestParam.getClass();
            Field[] fs = paramCls.getDeclaredFields();
            Map<String, String> getMap = new HashMap<>();
            if (fs != null && fs.length > 0) {
                for (Field f : fs) {
                    f.setAccessible(true);
                    String key = f.getName();
                    if (key.startsWith("$") || key.startsWith("serialVersionUID")) {
                        continue;
                    }
                    String value = null;
                    try {
                        value = f.get(requestParam).toString();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    getMap.put(key, value);

                }
            }
            RequestParams requestParams = new RequestParams(getMap);
            url = AsyncHttpClient.getUrlWithQueryString(url, requestParams);
        }
        Log.d(this.getClass().getSimpleName(), "url:" + url);
        HttpUriRequest request = new HttpGet(url);
        return request;
    }

    /**
     * 创建Post请求对象
     *
     * @param url
     * @param entity 普通实体对象
     * @return
     * @throws UnsupportedEncodingException
     * @author liuzhiyun
     */
    protected <T> HttpEntityEnclosingRequestBase buildPostRequest(String url,
                                                                  T entity) throws UnsupportedEncodingException {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        Log.d(this.getClass().getSimpleName(), "url:" + url);
        if (entity != null) {
            request.setEntity(getEntity(entity));
            request.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        }
        return request;
    }

    /**
     * 创建Post请求对象
     */
    protected <T> HttpEntityEnclosingRequestBase buildPostRequest(
            CPProtocolAction action, T entity)
            throws UnsupportedEncodingException {
        return buildPostRequest(action.url, entity);
    }

    /**
     *
     */
    protected <T> HttpEntity getEntity(T object)
            throws UnsupportedEncodingException {
        Gson gson = new Gson();
        String jsonParamString = gson.toJson(object);
        Log.d(this.getClass().getSimpleName(), "request params: "
                + jsonParamString);
        if (object instanceof RequestParam) {
            jsonParamString = ((RequestParam) object).pack(jsonParamString);
        }
        return new StringEntity(jsonParamString, HTTP.UTF_8);
    }

    /**
     * 解析请求结果，返回通用对象
     *
     * @param result
     * @return
     * @throws JSONException
     */
    public <DataType, MessageType, ControlType> TypedResult<DataType, MessageType, ControlType> parseResult(
            RequestParam requestParam,UrlParam urlParam, String result) throws JSONException {
        CPProtocolAction action = null;
        if (requestParam != null) {
            action = getAction(requestParam);
        }
        if (action == null && urlParam != null) {
            action = getAction(urlParam);
        }
        if (action == null) {
            throw new IllegalArgumentException(String.format(
                    "action not found: ", requestParam.getClass()));
        }
        if (urlParam != null) {
            result = urlParam.unpack(result);
        }
        if (requestParam != null) {
            result = requestParam.unpack(result);
        }
        return parseResult(result, action.resultType, action.messageType,
                action.controlType);
    }

    /**
     * 解析结果字符串，返回结果实体
     *
     * @param result     网络返回原始字符串
     * @param objectType 实体类名
     * @return
     * @throws JSONException
     */
    protected <DataType, MessageType, ControlType> TypedResult<DataType, MessageType, ControlType> parseResult(
            String result, Type objectType, Type messageType, Type controlType)
            throws JSONException {

        TypedResult<DataType, MessageType, ControlType> resultInfo = null;

        if (result == null) {
            result = "";
        }
        Log.d(this.getClass().getSimpleName(), "response result: " + result);
        resultInfo = new TypedResult<DataType, MessageType, ControlType>();

//        JSONObject json = new JSONObject(result);
        Gson gson = new Gson();


//        resultInfo.code = json.getInt(FIELD_RESULT_CODE);
//
//        if (messageType != null && messageType != Void.class) {
//            String message = json.has(FIELD_RESULT_MESSAGE) ? json
//                    .getString(FIELD_RESULT_MESSAGE) : null;
//            if (!TextUtils.isEmpty(message)) {
//                if (messageType == Object.class) {
//                    messageType = String.class;
//                }
//
//                resultInfo.msg = parseTypeJson(gson, message, messageType);
//
//                if (messageType == String.class) {
//                    resultInfo.message = (String) resultInfo.msg;
//                }
//            }
//        }
//
//        if (controlType != null && controlType != Void.class) {
//            String control = json.has(FIELD_RESULT_CONTROL) ? json
//                    .getString(FIELD_RESULT_CONTROL) : null;
//            if (!TextUtils.isEmpty(control)) {
//                resultInfo.ctrl = parseTypeJson(gson, control, controlType);
//            }
//        }
//
//        if (objectType != null && objectType != Void.class) {
//            String data = json.has(FIELD_RESULT_DATA) ? json
//                    .getString(FIELD_RESULT_DATA) : null;
//            if (!TextUtils.isEmpty(data)) {
//                resultInfo.obj = parseTypeJson(gson, data, objectType);
//            }
//        }
        if (objectType != null && objectType != Void.class) {
            resultInfo.obj = parseTypeJson(gson, result, objectType);
        }
        return resultInfo;
    }

    @SuppressWarnings("unchecked")
    private <ObjectType> ObjectType parseTypeJson(final Gson gson,
                                                  final String jsonStr, final Type objectType) throws JSONException {
        ObjectType obj = null;
        try {
            obj = (ObjectType) rawParseTypeJson(gson, jsonStr, objectType);
        } catch (TypeNotPresentException e) {
            try {
                obj = RunningEnvironment.callClass(new Callable<ObjectType>() {

                    @Override
                    public ObjectType call() throws Exception {
                        return (ObjectType) rawParseTypeJson(gson, jsonStr,
                                objectType);
                    }
                });
            } catch (Exception ex) {
                throw new JSONException("Type not found.");
            }
        }

        return obj;
    }

    @SuppressWarnings("unchecked")
    private <ObjectType> ObjectType rawParseTypeJson(Gson gson, String jsonStr,
                                                     Type objectType) {
        ObjectType obj = null;
        try {
            obj = (ObjectType) gson.fromJson(jsonStr, objectType);
        } catch (JsonSyntaxException e) {
            jsonStr = gson.toJson(jsonStr);
            try {
                obj = (ObjectType) gson.fromJson(jsonStr, objectType);
            } catch (JsonSyntaxException e2) {
                obj = null;
            }
        }

        return obj;
    }
}
