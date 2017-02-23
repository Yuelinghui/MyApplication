package com.yuelinghui.personal.network;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.text.TextUtils;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;
import com.yuelinghui.personal.maframe.cache.FileCache;
import com.yuelinghui.personal.maframe.cache.ImageMemoryCache;
import com.yuelinghui.personal.maframe.concurrent.Callbackable;
import com.yuelinghui.personal.maframe.concurrent.ThreadContext;
import com.yuelinghui.personal.maframe.exception.ThreadUncaughtExceptionHandler;
import com.yuelinghui.personal.maframe.result.Result;
import com.yuelinghui.personal.maframe.result.TypedResult;
import com.yuelinghui.personal.network.http.AsyncHttpClient;
import com.yuelinghui.personal.network.http.AsyncHttpResponseHandler;
import com.yuelinghui.personal.network.http.BinaryHttpResponseHandler;
import com.yuelinghui.personal.network.http.SyncHttpClient;
import com.yuelinghui.personal.network.mock.MockAsyncHttpClient;
import com.yuelinghui.personal.network.mock.MockConfig;
import com.yuelinghui.personal.network.mock.MockProtocol;
import com.yuelinghui.personal.network.mock.MockSSLSocketFactory;
import com.yuelinghui.personal.network.protocol.CustomProtocol;
import com.yuelinghui.personal.network.protocol.CustomProtocolAction;
import com.yuelinghui.personal.network.protocol.CustomProtocolGroup;
import com.yuelinghui.personal.network.protocol.CacheRequestParam;
import com.yuelinghui.personal.network.protocol.Request;
import com.yuelinghui.personal.network.protocol.RequestParam;
import com.yuelinghui.personal.network.protocol.UrlParam;

import java.io.UnsupportedEncodingException;

/**
 * Created by yuelinghui on 16/9/27.
 */

public class NetClient {


    /**
     * mock标识，用于调试
     */
    public static boolean mock = false;

    /**
     * 上下文
     */
    protected Context mContext = null;

    /**
     * 内存缓存
     */
    protected static ImageMemoryCache imageCache = null;
    /**
     * 文件缓存
     */
    protected static FileCache fileCache = null;

    /**
     * 协议组
     */
    protected static CustomProtocolGroup payProtocol = new CustomProtocolGroup();
    /**
     * 请求处理对象
     */
    protected static AsyncHttpClient asyncClient = new AsyncHttpClient();
    /**
     * 处理图片请求的对象
     */
    protected static AsyncHttpClient asyncImgClient = new AsyncHttpClient(10,
            8, 2);

    static {
        // 图像内存硬引用缓存容量，为系统可用内存的1/4
        if (imageCache == null) {
            int memClass = ((ActivityManager) RunningEnvironment.sAppContext
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            imageCache = new ImageMemoryCache(1024 * 1024 * memClass / 4);
        }

        // 文件存储路径
        if (fileCache == null) {
            String dir = RunningEnvironment.sAppContext.getCacheDir()
                    .toString();
            fileCache = new FileCache(dir);
        }
    }

    /**
     * 设置同步访问网络模式
     */
    public static void setSynchronizeMode() {
        asyncClient = new SyncHttpClient();
    }

    public NetClient() {
        this(RunningEnvironment.sAppContext);
    }

    @SuppressWarnings("deprecation")
    public NetClient(Context context) {
        mContext = context;

        if (mock) {
            try {
                MockSSLSocketFactory paySSL = new MockSSLSocketFactory(
                        RunningEnvironment.sAppContext);
                asyncClient.setSSLSocketFactory(paySSL);
                asyncImgClient.setSSLSocketFactory(paySSL);
            } catch (Exception e) {
                new ThreadUncaughtExceptionHandler().uncaughtException(e);
            }
        }
    }

    public static void addProtocol(CustomProtocol protocol) {
        synchronized (payProtocol) {
            protocol.load(payProtocol);
        }
    }

    /**
     * 添加模块对应Mock信息
     */
    public static void addMockProtocol(String module,
                                       MockProtocol mockProtocol, CustomProtocol protocol) {
        // FIXME release时直接抛异常，避免外部调用
        if (!mock) {
            throw new RuntimeException(
                    "NetClient.mock is false and don't support addMockProtocol");
        }

        MockConfig.addMockConfig(module, mockProtocol, protocol);
    }

    /**
     * 取消上下文相关的执行操作
     *
     * @param context
     */
    public static void cancelExecute(Context context) {
        asyncClient.cancelRequests(context, true);
        asyncImgClient.cancelRequests(context, true);
    }

    /**
     * 是否可重复的请求
     *
     * @param param
     * @return
     */
    public static boolean isRepeatableRequest(final RequestParam param) {
        CustomProtocolAction action = CustomProtocolGroup.getAction(param);
        return action != null && action.retry;
    }

    /**
     * Get请求的Task
     *
     * @param <DataType>
     * @param <MessageType>
     * @param <ControlType>
     */
    protected static class GetProtocolTask<DataType, MessageType, ControlType> {
        protected final RequestParam requestParam;
        protected final UrlParam urlParam;
        protected final Callbackable<Result<DataType>> resultCallbackable;
        protected final ProtocolGetHandler<DataType, MessageType, ControlType> responseHandler;
        protected Context context;

        public GetProtocolTask(Context context, RequestParam requestParam, UrlParam urlParam, Callbackable<Result<DataType>> callbackable) {
            this.context = context;
            this.requestParam = requestParam;
            this.urlParam = urlParam;
            this.resultCallbackable = callbackable;
            this.responseHandler = getResponseHandler();
        }

        /**
         * 执行请求
         */
        protected ProtocolGetHandler<DataType, MessageType, ControlType> getResponseHandler() {
            return new ProtocolGetHandler<DataType, MessageType, ControlType>(
                    requestParam, urlParam, resultCallbackable);
        }

        public void execute() {
            if (mock) {
                MockAsyncHttpClient.getInstance().payExecute(requestParam,
                        resultCallbackable);
                return;
            }

            try {
                Request request = buildRequest();
                asyncClient.get(context, request.httpRequest.getURI().toString(), responseHandler);
            } catch (Exception e) {
                if (resultCallbackable != null) {
                    resultCallbackable
                            .callback(new TypedResult<DataType, MessageType, ControlType>(
                                    Result.INTERNAL_EXCEPTION, Result.ERROR, e));
                }
                return;
            }
        }

        /**
         * 创建请求
         *
         * @return
         * @throws UnsupportedEncodingException
         */
        protected Request buildRequest() throws UnsupportedEncodingException {
            return payProtocol.buildGetRequest(requestParam, urlParam);
        }

    }

    /**
     * Post请求的Task
     *
     * @param <DataType, MessageType>
     * @author liuzhiyun
     */
    protected static class PostProtocolTask<DataType, MessageType, ControlType> {
        protected final RequestParam requestParam;
        protected final Callbackable<Result<DataType>> responseCallback;
        protected final ProtocolPostHandler<DataType, MessageType, ControlType> responseHandler;
        protected Context context;

        /**
         * @param context
         * @param param    request param
         * @param callback callback when response
         */
        public PostProtocolTask(Context context, RequestParam param,
                                Callbackable<Result<DataType>> callback) {
            this.context = context;
            requestParam = param;
            responseCallback = callback;
            responseHandler = getResponseHandler();
        }

        /**
         * 执行当前任务
         */
        public void execute() {
            if (mock) {
                MockAsyncHttpClient.getInstance().payExecute(requestParam,
                        responseCallback);
                return;
            }

            try {
                Request request = buildRequest();
                asyncClient.send(context, request.retry, request.httpRequest,
                        responseHandler);
            } catch (Exception e) {
                if (responseCallback != null) {
                    responseCallback
                            .callback(new TypedResult<DataType, MessageType, ControlType>(
                                    Result.INTERNAL_EXCEPTION, Result.ERROR, e));
                }
                return;
            }
        }

        /**
         * 创建请求
         *
         * @return
         * @throws UnsupportedEncodingException
         */
        protected Request buildRequest() throws UnsupportedEncodingException {
            return payProtocol.buildPostRequest(requestParam);
        }

        /**
         * 执行请求
         */
        protected ProtocolPostHandler<DataType, MessageType, ControlType> getResponseHandler() {
            return new ProtocolPostHandler<DataType, MessageType, ControlType>(
                    requestParam, responseCallback);
        }

    }


    /**
     * Get请求的Handler
     *
     * @param <DataType>
     * @param <MessageType>
     * @param <ControlType>
     */
    protected static class ProtocolGetHandler<DataType, MessageType, ControlType> extends AsyncHttpResponseHandler {
        protected final RequestParam requestParam;
        protected final Callbackable<Result<DataType>> resultCallbackable;
        protected final UrlParam urlParam;

        public ProtocolGetHandler(RequestParam param, UrlParam urlParam, Callbackable<Result<DataType>> callbackable) {
            requestParam = param;
            this.urlParam = urlParam;
            resultCallbackable = callbackable;
        }

        @Override
        protected void handleMessage(Message msg) {
            ThreadContext.check();
            super.handleMessage(msg);
        }

        @Override
        public void onSuccess(String content) {
            if (resultCallbackable != null) {
                TypedResult<DataType, MessageType, ControlType> result = null;
                if (TextUtils.isEmpty(content)) {
                    result = new TypedResult<DataType, MessageType, ControlType>(
                            Result.INTERNAL_EXCEPTION, Result.ERROR,
                            new Exception("content is null"));
                } else {
                    try {
                        result = payProtocol.parseResult(requestParam, urlParam, content);
                        if (onSuccess(result)) {
                            return;
                        }
                    } catch (Exception e) {
                        result = new TypedResult<DataType, MessageType, ControlType>(
                                Result.INTERNAL_DATA_ERROR, Result.ERROR, e);
                    }
                }

                resultCallbackable.callback(result);
            }
        }

        /**
         * 子类实现
         *
         * @param result true - 函数内部处理了逻辑，外部不继续处理; false - 外部继续处理
         */
        protected boolean onSuccess(
                TypedResult<DataType, MessageType, ControlType> result) {
            return false;
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);

            if (resultCallbackable != null) {
                resultCallbackable
                        .callback(new TypedResult<DataType, MessageType, ControlType>(
                                Result.INTERNAL_EXCEPTION, Result.ERROR, error));
            }
        }

        @Override
        public void onCancel() {
            super.onCancel();

            if (resultCallbackable != null) {
                resultCallbackable
                        .callback(new TypedResult<DataType, MessageType, ControlType>(
                                Result.INTERNAL_CANCELED, Result.ERROR,
                                new Exception("process cancelled")));
            }
        }

    }

    /**
     * 协议响应处理类
     *
     * @param <DataType, MessageType, ControlType>
     * @author liuzhiyun
     */
    protected static class ProtocolPostHandler<DataType, MessageType, ControlType>
            extends AsyncHttpResponseHandler {
        protected final RequestParam requestParam;
        protected final Callbackable<Result<DataType>> responseCallback;

        /**
         *
         */
        public ProtocolPostHandler(RequestParam param,
                                   Callbackable<Result<DataType>> callback) {
            requestParam = param;
            responseCallback = callback;
        }

        @Override
        protected void handleMessage(Message msg) {
            ThreadContext.check();

            super.handleMessage(msg);
        }

        @Override
        public void onSuccess(String content) {
            if (responseCallback != null) {
                TypedResult<DataType, MessageType, ControlType> result = null;
                if (TextUtils.isEmpty(content)) {
                    result = new TypedResult<DataType, MessageType, ControlType>(
                            Result.INTERNAL_EXCEPTION, Result.ERROR,
                            new Exception("content is null"));
                } else {
                    try {
                        result = payProtocol.parseResult(requestParam, null, content);
                        if (onSuccess(result)) {
                            return;
                        }
                    } catch (Exception e) {
                        result = new TypedResult<DataType, MessageType, ControlType>(
                                Result.INTERNAL_DATA_ERROR, Result.ERROR, e);
                    }
                }

                responseCallback.callback(result);
            }
        }

        /**
         * 子类实现
         *
         * @param result true - 函数内部处理了逻辑，外部不继续处理; false - 外部继续处理
         */
        protected boolean onSuccess(
                TypedResult<DataType, MessageType, ControlType> result) {
            return false;
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);

            if (responseCallback != null) {
                responseCallback
                        .callback(new TypedResult<DataType, MessageType, ControlType>(
                                Result.INTERNAL_EXCEPTION, Result.ERROR, error));
            }
        }

        @Override
        public void onCancel() {
            super.onCancel();

            if (responseCallback != null) {
                responseCallback
                        .callback(new TypedResult<DataType, MessageType, ControlType>(
                                Result.INTERNAL_CANCELED, Result.ERROR,
                                new Exception("process cancelled")));
            }
        }

    }

//    /**
//     * String响应处理类
//     */
//    protected static class StringResponseHandler extends
//            AsyncHttpResponseHandler {
//        protected final Callbackable<String> responseCallback;
//
//        /**
//         * @param callback callback when response
//         */
//        public StringResponseHandler(Callbackable<String> callback) {
//            responseCallback = callback;
//        }
//
//        @Override
//        protected void handleMessage(Message msg) {
//            ThreadContext.check();
//
//            super.handleMessage(msg);
//        }
//
//        @Override
//        public void onSuccess(String content) {
//            if (responseCallback != null) {
//                responseCallback.callback(content);
//            }
//        }
//
//        @Override
//        public void onFailure(Throwable error, String content) {
//            super.onFailure(error, content);
//
//            if (responseCallback != null) {
//                responseCallback.callback("");
//            }
//        }
//
//        @Override
//        public void onCancel() {
//            super.onCancel();
//
//            if (responseCallback != null) {
//                responseCallback.callback("");
//            }
//        }
//
//    }

    /**
     * 和支付相关http请求构造、执行、返回处理
     *
     * @param param
     * @param callback 保持和原有接口一致，返回Result<DataType>
     */
    public <DataType, MessageType, ControlType> void postExecute(
            final RequestParam param,
            final Callbackable<Result<DataType>> callback) {

        if (param instanceof CacheRequestParam) {
            if (mock) {
                MockAsyncHttpClient.getInstance().payExecute(param, callback);
                return;
            }
        } else {
            new PostProtocolTask<DataType, MessageType, ControlType>(mContext,
                    param, callback).execute();
        }
    }

    public <DataType, MessageType, ControlType> void getExecute(final RequestParam param, final UrlParam urlParam, final Callbackable<Result<DataType>> callbackable) {
        new GetProtocolTask<DataType, MessageType, ControlType>(mContext, param, urlParam, callbackable).execute();
    }
//
//    /**
//     * 和支付相关http请求构造、执行、返回处理
//     *
//     * @param param
//     * @param callback 保持和原有接口一致，返回DataType
//     */
//    public void rawPayExecute(final RequestParam param,
//                              final Callbackable<String> callback) {
//
//        if (mock) {
//            MockAsyncHttpClient.getInstance().rawPayExecute(param, callback);
//            return;
//        }
//
//        // create http request and handle the exception in current thread
//        // because it's not reasonable to add callback to protocol.
//        Request request = null;
//        try {
//            request = payProtocol.buildPostRequest(param);
//        } catch (Exception e) {
//            if (callback != null) {
//                callback.callback("");
//            }
//            return;
//        }
//
//        rawPayExecute(request, callback);
//    }

//    /**
//     * 和支付相关http请求构造、执行、返回处理
//     *
//     * @param callback 保持和原有接口一致，返回DataType
//     */
//    public void rawPayExecute(final Request request,
//                              final Callbackable<String> callback) {
//        try {
//            asyncClient.send(mContext, request.retry, request.httpRequest,
//                    new StringResponseHandler(callback));
//        } catch (Exception e) {
//            if (callback != null) {
//                callback.callback("");
//            }
//            return;
//        }
//    }

//    /**
//     * 和支付相关http同步请求构造、执行、返回处理
//     *
//     * @param param
//     */
//    public <DataType, MessageType, ControlType> TypedResult<DataType, MessageType, ControlType> postExecute(
//            final RequestParam param) {
//
//        if (mock) {
//            return MockAsyncHttpClient.getInstance().payExecute(param);
//        }
//
//        String response = null;
//        try {
//            Request request = payProtocol.buildPostRequest(param);
//            response = asyncClient.send(mContext, request.retry,
//                    request.httpRequest);
//            if (TextUtils.isEmpty(response)) {
//                throw new Exception("content is null");
//            }
//        } catch (Exception e) {
//            return new TypedResult<DataType, MessageType, ControlType>(
//                    Result.INTERNAL_EXCEPTION, Result.ERROR, e);
//        }
//
//        TypedResult<DataType, MessageType, ControlType> result = null;
//        try {
//            result = payProtocol.parseResult(param, response);
//        } catch (Exception e) {
//            return new TypedResult<DataType, MessageType, ControlType>(
//                    Result.INTERNAL_DATA_ERROR, Result.ERROR, e);
//        }
//
//        return result;
//    }

//    /**
//     * 和支付相关http同步请求构造、执行、返回处理
//     *
//     * @param param
//     */
//    public String rawPayExecute(final RequestParam param) {
//
//        if (mock) {
//            return MockAsyncHttpClient.getInstance().rawPayExecute(param);
//        }
//
//        Request request = null;
//        try {
//            request = payProtocol.buildPostRequest(param);
//        } catch (Exception e) {
//            return "";
//        }
//
//        return rawPayExecute(request);
//    }
//
//    /**
//     * 和支付相关http同步请求构造、执行、返回处理
//     */
//    public String rawPayExecute(final Request request) {
//        String response = null;
//        try {
//            response = asyncClient.send(mContext, request.retry,
//                    request.httpRequest);
//            if (TextUtils.isEmpty(response)) {
//                throw new Exception("content is null");
//            }
//        } catch (Exception e) {
//            return "";
//        }
//
//        return response;
//    }

//    /**
//     * 缓存本地文件， 只根据商户号和请求类名称做缓存， 根据时间先后去服务端取数据
//     *
//     * @param param
//     * @param callback
//     */
//    protected <DataType> void startTaskCache(CacheRequestParam param,
//                                             final Callbackable<Result<DataType>> callback) {
//
//        // 类型标识,标识定义(类名 + requseSign)
//        final String fileName = MD5.md5(new StringBuilder(param.getClass()
//                .getName()).append("_").append(param.getCacheId()).toString());
//
//        // 文件最后存储时间
//        Date fileLastDate = fileCache.getObjectDate(fileName);
//        if (fileLastDate != null) {
//            param.sysDataTime = new SimpleDateFormat("yyyyMMddHHmmss")
//                    .format(fileLastDate);
//        }
//
//        // 计数锁存器
//        final CountDownLatch countDownLatch = new CountDownLatch(1);
//
//        // 缓存存数据
//        final Result<DataType> resultCache = new Result<DataType>();
//
//        // 本地数据回调函数
//        Callbackable<Object> localCallbackable = new Callbackable<Object>() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            public void callback(Object result) {
//                resultCache.obj = (DataType) result;
//                // 计数减1，放开锁存器
//                countDownLatch.countDown();
//            }
//        };
//        fileCache.getObjectAsync(fileName, localCallbackable);
//
//        // 服务端取数据回调函数
//        final Callbackable<Result<DataType>> serviceCallbackable = new Callbackable<Result<DataType>>() {
//
//            @Override
//            public void callback(Result<DataType> result) {
//                if (result == null) {
//                    result = new Result<DataType>(Result.INTERNAL_EXCEPTION,
//                            Result.ERROR, "");
//                }
//
//                DataType saveObject = null;
//
//                // 处理服务端返回正确结果
//                if (result.code == Result.OK) {
//                    try {
//                        if (result.obj == null) {
//                            countDownLatch.await();
//                            // 返回本地数据
//                            result.obj = resultCache.obj;
//                        } else {
//                            saveObject = result.obj;
//                        }
//                    } catch (Exception e) {
//                        result = new Result<DataType>(
//                                Result.INTERNAL_EXCEPTION, Result.ERROR, e);
//                    }
//                }
//                if (callback != null) {
//                    callback.callback(result);
//                }
//
//                // 异步存储数据
//                // 1.在callback之后调用是为了外部callback数据合法性校验
//                // 2.在异步写入文件的过程中避免数据的修改
//                if (saveObject != null) {
//                    fileCache.setObjectAsync(fileName, saveObject);
//                    saveObject = null;
//                }
//            }
//
//        };
//        new TypedProtocolTask<DataType, String, Void>(mContext, param,
//                serviceCallbackable).execute();
//    }

    public void loadText(final String url,final Callbackable<Result<String>> callbackable) {
        final Result<String> result = new Result<>();
        if (TextUtils.isEmpty(url)) {
            result.setInternalError(Result.INTERNAL_EXCEPTION,"");
            callbackable.callback(result);
            return;
        }
        String textUrl = url;
        if (!url.startsWith("http")) {
            textUrl = "http://" + textUrl;
        }
        String string = (String) fileCache.getObject(textUrl);
        if (string != null) {
            result.code = Result.OK;
            result.obj = string;
            callbackable.callback(result);
            return;
        }

        String fTextUrl = textUrl;
        try {
            loadTextStart(fTextUrl, result, callbackable);
        } catch (Exception e) {
            result.setInternalError(Result.INTERNAL_EXCEPTION, "");
            callbackable.callback(result);
            return;
        }
    }

    /**
     * 图片下载
     *
     * @param url      the URL to send the request to.
     * @param result   the object of result
     * @param callback callback for result
     * @author zfx
     */
    private void loadTextStart(final String url, final Result<String> result,
                           final Callbackable<Result<String>> callback) {

        asyncImgClient.get(mContext, url, new BinaryHttpResponseHandler() {

            @Override
            public void onSuccess(byte[] binaryData) {
                String text = null;
                try {
                    text = new String(binaryData,"utf-8");

                    fileCache.setObject(url,text);

                    result.code = Result.OK;
                    result.obj = text;
                } catch (Exception e) {
                    result.setInternalError(Result.INTERNAL_DATA_ERROR, e);
                }
                callback.callback(result);
            }

            @Override
            public void onFailure(Throwable error, String localFilePath) {
                super.onFailure(error, localFilePath);

                result.setInternalError(Result.INTERNAL_DATA_ERROR, error);
                callback.callback(result);
            }

            @Override
            public void onCancel() {
                super.onCancel();

                result.setInternalError(Result.INTERNAL_CANCELED, "");
                callback.callback(result);
            }

        });
    }
    /**
     * 异步下载图片
     *
     * @param url
     */
    public void loadImage(final String url,
                          final Callbackable<Result<Bitmap>> callback) {

        final Result<Bitmap> result = new Result<Bitmap>();

        // 地址为空，返回错误图片
        if (TextUtils.isEmpty(url)) {
            result.setInternalError(Result.INTERNAL_EXCEPTION, "");
            callback.callback(result);
            return;
        }

        String imgUrl = url;
        if (!url.startsWith("http")) {
            imgUrl = "http://" + imgUrl;
        }

        // 从内存缓存中获取图片
        Bitmap bitmap = imageCache.getBitmap(imgUrl);
        if (bitmap != null) {
            result.code = Result.OK;
            result.obj = bitmap;
            callback.callback(result);
            return;
        }

        // 从文件缓存中取
        bitmap = fileCache.getImage(imgUrl);
        if (bitmap != null) {
            // 添加到内存缓存
            imageCache.saveBitmap(imgUrl, bitmap);
            result.code = Result.OK;
            result.obj = bitmap;
            callback.callback(result);
            return;
        }

        // firefox_test image url
        // 图片服务器放到内网时，使用此代码
        // imgUrl = imgUrl.replaceAll("img.*.360buyimg.com", "192.168.200.228");
        // firefox_test
        final String fImgUrl = imgUrl;
        try {
            loadStart(fImgUrl, result, callback);
        } catch (Exception e) {
            result.setInternalError(Result.INTERNAL_EXCEPTION, "");
            callback.callback(result);
            return;
        }

    }

    /**
     * 同步请求缓存图片，不存在则返回空
     *
     * @param url
     */
    public Bitmap loadImageFromCache(final String url) {

        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String imgUrl = url;
        if (!url.startsWith("http")) {
            imgUrl = "http://" + imgUrl;
        }

        Bitmap bitmap = imageCache.getBitmap(imgUrl);
        if (bitmap == null) {
            bitmap = fileCache.getImage(imgUrl);
            if (bitmap != null) {
                imageCache.saveBitmap(imgUrl, bitmap);
            }
        }

        return bitmap;
    }

    /**
     * 图片下载
     *
     * @param url      the URL to send the request to.
     * @param result   the object of result
     * @param callback callback for result
     * @author zfx
     */
    private void loadStart(final String url, final Result<Bitmap> result,
                           final Callbackable<Result<Bitmap>> callback) {

        asyncImgClient.get(mContext, url, new BinaryHttpResponseHandler() {

            @Override
            public void onSuccess(byte[] binaryData) {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeByteArray(binaryData, 0,
                            binaryData.length);

                    fileCache.setImageAsync(url, bitmap);
                    imageCache.saveBitmap(url, bitmap);

                    result.code = Result.OK;
                    result.obj = bitmap;
                } catch (Exception e) {
                    result.setInternalError(Result.INTERNAL_DATA_ERROR, e);
                }
                callback.callback(result);
            }

            @Override
            public void onFailure(Throwable error, String localFilePath) {
                super.onFailure(error, localFilePath);

                result.setInternalError(Result.INTERNAL_DATA_ERROR, error);
                callback.callback(result);
            }

            @Override
            public void onCancel() {
                super.onCancel();

                result.setInternalError(Result.INTERNAL_CANCELED, "");
                callback.callback(result);
            }

        });
    }


}
