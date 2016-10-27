package com.yuelinghui.personal.maframe.result;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class Result<DataType> {

    /**
     * 成功
     */
    public static final int OK = 0;
    /**
     * 异常
     */
    public static final int INTERNAL_EXCEPTION = 11;
    /**
     * 取消
     */
    public static final int INTERNAL_CANCELED = 12;
    /**
     * 数据解析错误
     */
    public static final int INTERNAL_DATA_ERROR = 13;
    /**
     * 中断错误
     */
    public static final int INTERNAL_INTERRUPT = 14;

    /**
     * 错误（内部缺省错误，如果和业务码有冲突，可以替换）
     */
    public static final int ERROR = 1;
    /**
     * 需短信确认，协议定义
     */
    public static final int NEXT_SMS = 2;
    /**
     * 敬请期待，协议定义
     */
    public static final int WAITING = 1024;
    /**
     * 结果码
     */
    public int code = OK;
    /**
     * 结果信息
     */
    public String message = "";
    /**
     * 结果对象
     */
    public DataType obj = null;

    /**
     * 内部结果码
     */
    private int mInternalError = OK;
    /**
     * 内部异常对象，包含详细堆栈信息
     */
    private Throwable mInternalException = null;

    /**
     * Constructor
     */
    public Result() {
    }

    /**
     * Constructor
     *
     * @param code
     */
    public Result(int code) {
        this.code = code;
    }

    /**
     * Constructor
     *
     * @param code
     * @param message
     */
    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor
     *
     * @param code
     * @param e
     */
    public Result(int code, Throwable e) {
        this(Result.OK, code, e);
    }

    /**
     * Constructor
     *
     * @param internalError 内部错误码
     * @param code          业务码
     */
    public Result(int internalError, int code, String message) {
        this.mInternalError = internalError;
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor
     *
     * @param internalError 内部错误码
     * @param code          业务码
     */
    public Result(int internalError, int code, Throwable internalException) {
        this.mInternalError = internalError;
        this.code = code;
        this.mInternalException = internalException;
    }

    /**
     * 是否存在内部错误
     *
     * @return
     */
    public boolean existInternalError() {
        return mInternalError != TypedResult.OK;
    }

    /**
     * 内部错误码
     *
     * @return
     */
    public int internalError() {
        return mInternalError;
    }

    /**
     * 内部错误码
     *
     * @return
     */
    public Throwable internalException() {
        return mInternalException;
    }

    /**
     * 设置内部错误，错误提示内部定义
     *
     * @param internalError
     */
    public void setInternalError(int internalError) {
        this.mInternalError = internalError;
        this.code = ERROR;
    }

    /**
     * 设置内部错误
     *
     * @param internalError
     * @param internalException
     */
    public void setInternalError(int internalError, Throwable internalException) {
        this.mInternalError = internalError;
        this.code = ERROR;
        this.mInternalException = internalException;
    }

    /**
     * 设置内部错误
     *
     * @param internalError
     * @param message
     */
    public void setInternalError(int internalError, String message) {
        this.mInternalError = internalError;
        this.code = ERROR;
        this.message = message;
    }

    /**
     * 设置内部错误
     *
     * @param internalError
     * @param code
     * @param message
     */
    public void setInternalError(int internalError, int code, String message) {
        this.mInternalError = internalError;
        this.code = code;
        this.message = message;
    }

}
