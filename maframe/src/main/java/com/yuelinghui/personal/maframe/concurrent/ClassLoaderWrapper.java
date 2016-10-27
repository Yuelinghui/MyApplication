package com.yuelinghui.personal.maframe.concurrent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by yuelinghui on 16/8/30.
 * ClassLoader包装类
 */

public class ClassLoaderWrapper extends ClassLoader {

    private ClassLoader mBase;

    public ClassLoaderWrapper(ClassLoader baseClassLoader) {
        mBase = baseClassLoader;
    }

    /**
     * 获取绑定的ClassLoader
     *
     * @return
     */
    public ClassLoader getBaseClassLoader() {
        return mBase;
    }

    /**
     * 设置ClassLoader
     *
     * @param baseClassLoader
     */
    public void setBaseClassLoader(ClassLoader baseClassLoader) {
        mBase = baseClassLoader;
    }

    @Override
    public void clearAssertionStatus() {
        mBase.clearAssertionStatus();
    }

    @Override
    public URL getResource(String resName) {
        return mBase.getResource(resName);
    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        return mBase.getResourceAsStream(resName);
    }

    @Override
    public Enumeration<URL> getResources(String resName) throws IOException {
        return mBase.getResources(resName);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return mBase.loadClass(className);
    }

    @Override
    public void setClassAssertionStatus(String cname, boolean enable) {
        mBase.setClassAssertionStatus(cname, enable);
    }

    @Override
    public void setDefaultAssertionStatus(boolean enable) {
        mBase.setDefaultAssertionStatus(enable);
    }

    @Override
    public void setPackageAssertionStatus(String pname, boolean enable) {
        mBase.setPackageAssertionStatus(pname, enable);
    }
}
