package com.yuelinghui.personal.maframe.result;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import com.yuelinghui.personal.maframe.concurrent.CancelListener;

import java.lang.ref.WeakReference;

/**
 * Created by yuelinghui on 16/8/30.
 */
public abstract class ResultHandler<DataType> implements ResultNotifier<DataType>, CancelListener {

    /**
     * 成功消息
     */
    protected static final int MESSAGE_SUCCESS = 0;
    /**
     * 失败消息
     */
    protected static final int MESSAGE_FAILURE = 1;
    /**
     * 中断消息
     */
    protected static final int MESSAGE_INTERRUPT = 2;
    /**
     * 取消消息
     */
    protected static final int MESSAGE_CANCELLED = 3;
    /**
     * 需要短信校验
     */
    protected static final int MESSAGE_SMS = 11;
    /**
     * 敬请期待
     */
    protected static final int MESSAGE_WAITING = 12;
    /**
     * 校验失败
     */
    protected static final int MESSAGE_VERIFY_FAILURE = 13;
    /**
     * 结束
     */
    protected static final int MESSAGE_FINISH = 14;
    /**
     * 最近5个已取消的结果处理器
     */
    private static LruCache<Integer, Long> sCancelHandlers = new LruCache<Integer, Long>(5);
    /**
     * 相关的上下文
     */
    private WeakReference<Context> mContextRef = null;
    /**
     * 时间戳，可用于cancel记录时间
     */
    private long mTimeStamp = 0;
    /**
     * 嵌套层次，嵌套的onStart和onFinish都不做处理
     */
    private int mNestedLevel;

    /**
     * 通知结果处理需要取消
     *
     * @param context
     * @return
     */
    public static void cancel(Context context) {
        if (context == null) {
            return;
        }

        synchronized (sCancelHandlers) {
            sCancelHandlers.put(context.hashCode(), System.currentTimeMillis());
        }
    }

    /**
     * 查看Handler是否取消
     *
     * @param context
     * @param timeStamp
     * @return
     */
    private static boolean isCanceled(Context context, long timeStamp) {
        if (context == null) {
            return false;
        }

        boolean cancel = false;
        synchronized (sCancelHandlers) {
            Long cancleTimeStamp = sCancelHandlers.get(context.hashCode());
            cancel = cancleTimeStamp != null
                    && cancleTimeStamp.longValue() > timeStamp;
        }
        return cancel;
    }

    /**
     * handler消息到主线程
     */
    protected Handler mHandler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {

            Context context = null;
            if (mContextRef != null) {
                context = mContextRef.get();
                if (context == null) {
                    return;
                } else if (context instanceof Activity
                        && ((Activity) context).isFinishing()) {
                    return;
                } else if (isCanceled(context, mTimeStamp)) {
                    return;
                }
            }

            switch (msg.what) {
                case MESSAGE_SUCCESS: {
                    Object[] params = (Object[]) msg.obj;
                    onSuccess((DataType) params[0], (String) params[1]);
                    finish();
                    break;
                }
                case MESSAGE_FAILURE: {
                    Object[] params = (Object[]) msg.obj;
                    onFailure((Integer) params[0], (String) params[1]);
                    finish();
                    break;
                }
                case MESSAGE_INTERRUPT: {
                    Object[] params = (Object[]) msg.obj;
                    if (context instanceof OnResultInterruptListener) {
                        ((OnResultInterruptListener) context).onResultInterrupt(
                                (Integer) params[0], (String) params[1]);
                    }
                    finish();
                    break;
                }
                case MESSAGE_SMS: {
                    Object[] params = (Object[]) msg.obj;
                    onSMS((DataType) params[0], (String) params[1]);
                    finish();
                    break;
                }
                case MESSAGE_WAITING: {
                    onWaiting((String) msg.obj);
                    finish();
                    break;
                }
                case MESSAGE_VERIFY_FAILURE: {
                    onVerifyFailure((String) msg.obj);
                    finish();
                    break;
                }
                case MESSAGE_CANCELLED:
                    onCancelled();
                    finish();
                    break;
                case MESSAGE_FINISH:
                    onFinish();
                    break;
                default:
                    handleExternalMessage(msg);
                    break;
            }
        }

    };

    /**
     * 处理外部信息
     *
     * @param msg
     */
    protected void handleExternalMessage(Message msg) {
    }

    @Override
    public boolean prepare(Context context) {
        if (mNestedLevel > 0) {
            return true;
        }
        if (context != null) {
            mContextRef = new WeakReference<Context>(context);
        } else {
            mContextRef = null;
        }
        mTimeStamp = System.currentTimeMillis();

        mNestedLevel++;
        return onStart();
    }

    /**
     * 处理结束
     */
    protected void finish() {
        mNestedLevel = 0;
        onFinish();
    }

    /**
     * 取消处理
     */
    @Override
    public void onCancel(int cancelType) {
        onCancelled();
        onFinish();
    }

    @Override
    public final void notifySuccess(DataType data, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SUCCESS,
                new Object[] { data, message }));
    }

    @Override
    public final void notifyFailure(int resultCode, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_FAILURE,
                new Object[] { resultCode, message }));
    }

    @Override
    public final void notifyInterrupt(int resultCode, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_INTERRUPT,
                new Object[] { resultCode, message }));
    }

    @Override
    public final void notifySMS(DataType data, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SMS, new Object[] {
                data, message }));
    }

    @Override
    public final void notifyWaiting(String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WAITING, message));
    }

    @Override
    public final void notifyVerifyFailure(String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_VERIFY_FAILURE,
                message));
    }

    @Override
    public final void notifyCancel() {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_CANCELLED));
    }

    @Override
    public final void notifyFinish() {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_FINISH));
    }

    /**
     * 成功结果处理
     *
     * @param data
     * @param message
     */
    protected void onSuccess(DataType data, String message) {
    };

    /**
     * 错误结果处理
     *
     * @param message
     *            为空代表不提示
     */
    protected void onFailure(int resultCode, String message) {
    };

    /**
     * 短消息验证处理
     *
     * @param message
     */
    protected void onSMS(DataType data, String message) {
    };

    /**
     * 敬请期待
     *
     * @param message
     */
    protected void onWaiting(String message) {
        onVerifyFailure(message);
    };

    /**
     * 校验验证处理
     *
     * @param message
     */
    protected void onVerifyFailure(String message) {
        onFailure(1, message);
    };

    /**
     * 取消处理
     */
    protected void onCancelled() {
    };

    /**
     * 结果处理准备
     *
     */
    protected abstract boolean onStart();

    /**
     * 结果处理完毕
     *
     */
    protected abstract void onFinish();
}
