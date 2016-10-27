package com.yuelinghui.personal.maframe.concurrent;

import com.yuelinghui.personal.maframe.are.RunningEnvironment;

import java.lang.ref.WeakReference;

/**
 * Created by yuelinghui on 16/8/30.
 * 线程上下文工具类，常用于上下文切换等场景
 */
public class ThreadContext {

    private static WeakReference<ClassLoaderWrapper> classLoaderWtapper = new WeakReference<ClassLoaderWrapper>(null);

    /**
     * 根据指定的上下文，维系线程上下文，在线程运行前调用
     */
    public static synchronized void check() {
        Thread currentThread = Thread.currentThread();
        ClassLoader contextClassLoader = RunningEnvironment.sAppContext.getClassLoader();
        ClassLoaderWrapper wrapper = classLoaderWtapper.get();

        if (wrapper != null) {
            wrapper.setBaseClassLoader(contextClassLoader);
        } else {
            wrapper = new ClassLoaderWrapper(contextClassLoader);
            classLoaderWtapper = new WeakReference<ClassLoaderWrapper>(wrapper);
        }
        currentThread.setContextClassLoader(wrapper);
    }

    /**
     * 获取线程真正的ClassLoader
     *
     * @return
     */
    public static synchronized ClassLoader getActrualCalssLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        if (contextClassLoader instanceof ClassLoaderWrapper) {
            return ((ClassLoaderWrapper) contextClassLoader).getBaseClassLoader();
        }
        return contextClassLoader;
    }
}
