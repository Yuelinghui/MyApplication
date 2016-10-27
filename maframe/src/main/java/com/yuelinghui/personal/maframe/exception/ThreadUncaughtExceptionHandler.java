package com.yuelinghui.personal.maframe.exception;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class ThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public ThreadUncaughtExceptionHandler() {
    }

    public void uncaughtException(Exception e) {
        uncaughtException(Thread.currentThread(), e);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex != null) {
            StringBuilder sb = new StringBuilder();
            String temp = ex.getMessage();
            if (temp != null) {
                sb.append(temp);
            }
            sb.append("\r\n");
            sb.append(thread.getName());
            sb.append(" Trace: \r\n");
            StackTraceElement[] elements = ex.getStackTrace();
            if (elements != null) {
                for (StackTraceElement element : elements) {
                    temp = element.toString();
                    if (temp != null) {
                        sb.append(temp);
                    }
                    sb.append("\r\n");
                }
            }

            // if the exception was thrown in a background thread inside
            // AsyncTask, then the actual exception can be found with getCause
            sb.append("Cause: ");
            Throwable theCause = ex.getCause();
            if (theCause != null) {
                temp = theCause.toString();
                if (temp != null) {
                    sb.append(temp);
                }
            }
            sb.append("\r\nCause Stack:\r\n");
            theCause = ex.getCause();
            if (theCause != null) {
                elements = theCause.getStackTrace();
                if (elements != null) {
                    for (StackTraceElement element : elements) {
                        temp = element.toString();
                        if (temp != null) {
                            sb.append(temp);
                        }
                        sb.append("\r\n");
                    }
                }
            }
        }
    }
}
