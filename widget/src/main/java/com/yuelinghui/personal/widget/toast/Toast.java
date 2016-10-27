package com.yuelinghui.personal.widget.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.yuelinghui.personal.widget.R;

import java.util.ArrayList;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class Toast {

    static final String TAG = "Toast";
    static final boolean localLOGV = false;

    /**
     * Show the view or text notification for a short period of time. This time
     * could be user-definable. This is the default.
     *
     * @see #setDuration
     */
    public static final int LENGTH_SHORT = 0;

    /**
     * Show the view or text notification for a long period of time. This time
     * could be user-definable.
     *
     * @see #setDuration
     */
    public static final int LENGTH_LONG = 1;

    final Context mContext;
    final TN mTN;
    int mDuration;
    View mNextView;

    /**
     * Construct an empty Toast object. You must call {@link #setView} before
     * you can call {@link #show}.
     *
     * @param context
     *            The context to use. Usually your
     *            {@link android.app.Application} or
     *            {@link android.app.Activity} object.
     */
    public Toast(Context context) {
        mContext = context;
        mTN = new TN();
        // mTN.mY = context.getResources().getDimensionPixelSize(
        // com.android.internal.R.dimen.toast_y_offset);
        mTN.mY = context.getResources().getDimensionPixelSize(
                R.dimen.toast_y_offset);
        // mTN.mGravity = context.getResources().getInteger(
        // com.android.internal.R.integer.config_toastDefaultGravity);
        mTN.mGravity = Gravity.CENTER;
    }

    /**
     * Show the view for the specified duration.
     */
    public void show() {
        if (mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }

        TN tn = mTN;
        tn.mNextView = mNextView;

        try {
            enqueueToast(tn, mDuration);
        } catch (Exception e) {
            // Empty
        }
    }

    /**
     * Close the view if it's showing, or don't show it if it isn't showing yet.
     * You do not normally have to call this. Normally view will disappear on
     * its own after the appropriate duration.
     */
    public void cancel() {
        mTN.hide();

        try {
            cancelToast(mTN);
        } catch (Exception e) {
            // Empty
        }
    }

    /**
     * Set the view to show.
     *
     * @see #getView
     */
    public void setView(View view) {
        mNextView = view;
        mTN.mNextView = view;
    }

    /**
     * Return the view.
     *
     * @see #setView
     */
    public View getView() {
        return mNextView;
    }

    /**
     * Set how long to show the view for.
     *
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    /**
     * Return the duration.
     *
     * @see #setDuration
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * Set the margins of the view.
     *
     * @param horizontalMargin
     *            The horizontal margin, in percentage of the container width,
     *            between the container's edges and the notification
     * @param verticalMargin
     *            The vertical margin, in percentage of the container height,
     *            between the container's edges and the notification
     */
    public void setMargin(float horizontalMargin, float verticalMargin) {
        mTN.mHorizontalMargin = horizontalMargin;
        mTN.mVerticalMargin = verticalMargin;
    }

    /**
     * Return the horizontal margin.
     */
    public float getHorizontalMargin() {
        return mTN.mHorizontalMargin;
    }

    /**
     * Return the vertical margin.
     */
    public float getVerticalMargin() {
        return mTN.mVerticalMargin;
    }

    /**
     * Set the location at which the notification should appear on the screen.
     *
     * @see android.view.Gravity
     * @see #getGravity
     */
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mTN.mGravity = gravity;
        mTN.mX = xOffset;
        mTN.mY = yOffset;
    }

    /**
     * Get the location at which the notification should appear on the screen.
     *
     * @see android.view.Gravity
     * @see #getGravity
     */
    public int getGravity() {
        return mTN.mGravity;
    }

    /**
     * Return the X offset in pixels to apply to the gravity's location.
     */
    public int getXOffset() {
        return mTN.mX;
    }

    /**
     * Return the Y offset in pixels to apply to the gravity's location.
     */
    public int getYOffset() {
        return mTN.mY;
    }

    private static class TN {
        // private static class TN extends ITransientNotification.Stub {
        final Runnable mShow = new Runnable() {
            @Override
            public void run() {
                handleShow();
            }
        };

        final Runnable mHide = new Runnable() {
            @Override
            public void run() {
                handleHide();
                // Don't do this in handleHide() because it is also invoked by
                // handleShow()
                mNextView = null;
            }
        };

        private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        public static final WorkerHandler mHandler = new WorkerHandler();

        int mGravity;
        int mX, mY;
        float mHorizontalMargin;
        float mVerticalMargin;

        View mView;
        View mNextView;

        WindowManager mWM;

        TN() {
            // XXX This should be changed to use a Dialog, with a Theme.Toast
            // defined that sets up the layout params appropriately.
            final WindowManager.LayoutParams params = mParams;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;
            params.windowAnimations = android.R.style.Animation_Toast;
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.setTitle("Toast");
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }

        /**
         * schedule handleShow into the right thread
         */
        public void show() {
            if (localLOGV)
                Log.v(TAG, "SHOW: " + this);
            mHandler.post(mShow);
        }

        /**
         * schedule handleHide into the right thread
         */
        public void hide() {
            if (localLOGV)
                Log.v(TAG, "HIDE: " + this);
            mHandler.post(mHide);
        }

        @SuppressLint("InlinedApi")
        public void handleShow() {
            if (localLOGV)
                Log.v(TAG, "HANDLE SHOW: " + this + " mView=" + mView
                        + " mNextView=" + mNextView);
            if (mView != mNextView) {
                // remove the old view if necessary
                handleHide();
                mView = mNextView;
                Context context = mView.getContext().getApplicationContext();
                if (context == null) {
                    context = mView.getContext();
                }
                mWM = (WindowManager) context
                        .getSystemService(Context.WINDOW_SERVICE);
                // We can resolve the Gravity here by using the Locale for
                // getting
                // the layout direction
                int gravity = mGravity;
                int layoutDirection = 0;
                if (Build.VERSION.SDK_INT >= 17) {
                    try {
                        layoutDirection = View.LAYOUT_DIRECTION_LTR;
                        final Configuration config = mView.getContext()
                                .getResources().getConfiguration();
                        layoutDirection = config.getLayoutDirection();
                    } catch (Exception e) {
                    }
                }
                if (Build.VERSION.SDK_INT >= 14) {
                    try {
                        gravity = Gravity.getAbsoluteGravity(mGravity,
                                layoutDirection);
                    } catch (Exception e) {
                    }
                }
                mParams.gravity = gravity;
                if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                    mParams.horizontalWeight = 1.0f;
                }
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                    mParams.verticalWeight = 1.0f;
                }
                mParams.x = mX;
                mParams.y = mY;
                mParams.verticalMargin = mVerticalMargin;
                mParams.horizontalMargin = mHorizontalMargin;
                if (mView.getParent() != null) {
                    if (localLOGV)
                        Log.v(TAG, "REMOVE! " + mView + " in " + this);
                    mWM.removeView(mView);
                }
                if (localLOGV)
                    Log.v(TAG, "ADD! " + mView + " in " + this);
                mWM.addView(mView, mParams);
            }
        }

        public void handleHide() {
            if (localLOGV)
                Log.v(TAG, "HANDLE HIDE: " + this + " mView=" + mView);
            if (mView != null) {
                // note: checking parent() just to make sure the view has
                // been added... i have seen cases where we get here when
                // the view isn't yet added, so let's try not to crash.
                if (mView.getParent() != null) {
                    if (localLOGV)
                        Log.v(TAG, "REMOVE! " + mView + " in " + this);
                    mWM.removeView(mView);
                }

                mView = null;
            }
        }
    }

    // Toasts，以下代码参考自系统的NotificationManagerService
    // ============================================================================

    // message codes
    private static final int MESSAGE_TIMEOUT = 2;

    private static final int LONG_DELAY = 3500; // 3.5 seconds
    private static final int SHORT_DELAY = 2000; // 2 seconds

    private static ArrayList<ToastRecord> mToastQueue = new ArrayList<ToastRecord>();

    /**
     * 安全退出，强制从界面删除toast，避免应用退出，而Toast一直显示
     */
    public static void exit() {
        synchronized (mToastQueue) {
            try {
                for (ToastRecord r : mToastQueue) {
                    r.callback.handleHide();
                }
            } catch (Exception e) {
            }
            mToastQueue.clear();
        }
    }

    public static void enqueueToast(TN callback, int duration) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }

        synchronized (mToastQueue) {
            try {
                ToastRecord record;
                int index = indexOfToastLocked(callback);
                // If it's already in the queue, we update it in place, we don't
                // move it to the end of the queue.
                if (index >= 0) {
                    record = mToastQueue.get(index);
                    record.update(duration);
                } else {
                    // Limit the number of toasts that any given package except
                    // the android
                    // package can enqueue. Prevents DOS attacks and deals with
                    // leaks.
                    record = new ToastRecord(callback, duration);
                    mToastQueue.add(record);
                    index = mToastQueue.size() - 1;
                }
                // If it's at index 0, it's the current toast. It doesn't matter
                // if it's
                // new or just been updated. Call back and tell it to show
                // itself.
                // If the callback fails, this will remove it from the list, so
                // don't
                // assume that it's valid after this.
                if (index == 0) {
                    showNextToastLocked();
                }
            } catch (Exception e) {
            } finally {
            }
        }
    }

    public static void cancelToast(TN callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }

        synchronized (mToastQueue) {
            try {
                int index = indexOfToastLocked(callback);
                if (index >= 0) {
                    cancelToastLocked(index);
                }
            } catch (Exception e) {
            } finally {
            }
        }
    }

    private static void showNextToastLocked() {
        ToastRecord record = mToastQueue.get(0);
        while (record != null) {
            try {
                record.callback.show();
                scheduleTimeoutLocked(record);
                return;
            } catch (Exception e) {
                // remove it from the list and let the process die
                int index = mToastQueue.indexOf(record);
                if (index >= 0) {
                    mToastQueue.remove(index);
                }
                if (mToastQueue.size() > 0) {
                    record = mToastQueue.get(0);
                } else {
                    record = null;
                }
            }
        }
    }

    private static void cancelToastLocked(int index) {
        ToastRecord record = mToastQueue.get(index);
        try {
            record.callback.hide();
        } catch (Exception e) {
            // don't worry about this, we're about to remove it from
            // the list anyway
        }
        mToastQueue.remove(index);
        if (mToastQueue.size() > 0) {
            // Show the next one. If the callback fails, this will remove
            // it from the list, so don't assume that the list hasn't changed
            // after this point.
            showNextToastLocked();
        }
    }

    private static void scheduleTimeoutLocked(ToastRecord r) {
        TN.mHandler.removeCallbacksAndMessages(r);
        Message m = Message.obtain(TN.mHandler, MESSAGE_TIMEOUT, r);
        long delay = r.duration == Toast.LENGTH_LONG ? LONG_DELAY : SHORT_DELAY;
        TN.mHandler.sendMessageDelayed(m, delay);
    }

    private static void handleTimeout(ToastRecord record) {
        synchronized (mToastQueue) {
            int index = indexOfToastLocked(record.callback);
            if (index >= 0) {
                cancelToastLocked(index);
            }
        }
    }

    // lock on mToastQueue
    private static int indexOfToastLocked(TN callback) {
        ArrayList<ToastRecord> list = mToastQueue;
        int len = list.size();
        for (int i = 0; i < len; i++) {
            ToastRecord r = list.get(i);
            if (r.callback == callback) {
                return i;
            }
        }
        return -1;
    }

    private static final class ToastRecord {
        final TN callback;
        int duration;

        ToastRecord(TN callback, int duration) {
            this.callback = callback;
            this.duration = duration;
        }

        void update(int duration) {
            this.duration = duration;
        }

        @Override
        public final String toString() {
            return "ToastRecord{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " callback=" + callback + " duration=" + duration;
        }
    }

    private static final class WorkerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TIMEOUT:
                    Toast.handleTimeout((ToastRecord) msg.obj);
                    break;
            }
        }
    }
}
