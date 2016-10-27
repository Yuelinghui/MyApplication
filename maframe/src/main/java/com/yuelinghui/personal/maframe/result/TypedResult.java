package com.yuelinghui.personal.maframe.result;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class TypedResult<DataType, MessageType, ControlType> extends Result<DataType> {

    /**
     * 信息对象
     */
    public MessageType msg = null;

    /**
     * 流程控制对象
     */
    public ControlType ctrl = null;

    /**
     * Constructor
     */
    public TypedResult() {
    }

    /**
     * Constructor
     *
     * @param code
     */
    public TypedResult(int code) {
        super(code);
    }

    /**
     * Constructor
     *
     * @param internalError 内部错误码
     * @param code          业务码
     */
    public TypedResult(int internalError, int code, String message) {
        super(internalError, code, message);
    }

    /**
     * Constructor
     *
     * @param internalError 内部错误码
     * @param code          业务码
     */
    public TypedResult(int internalError, int code, Throwable internalException) {
        super(internalError, code, internalException);
    }

}
