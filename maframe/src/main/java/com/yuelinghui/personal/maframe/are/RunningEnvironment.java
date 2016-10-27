package com.yuelinghui.personal.maframe.are;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by yuelinghui on 16/8/30.
 */
public class RunningEnvironment {

    /**
     * 全局静态context
     */
    public static Context sAppContext = null;

    /**
     * 网络连接管理
     */
    public static ConnectivityManager sConnectManager = null;

    /**
     * 是否合法环境
     */
    public static boolean sIsValidEnvionment = true;

    /**
     * SDCard status
     */
    public static boolean sIsValidSDCard = true;

    /**
     * 移动设备的唯一标识
     */
    private static String IMEI = null;
    /**
     * MAC地址，用于模拟IMEI
     */
    private static String MAC = null;
    /**
     * SIM卡唯一标识
     */
    private static String IMSI = null;
    /**
     * 手机号码
     */
    private static String PHONE_NUMBER = null;

    /**
     * 全局线程池
     */
    private static ThreadPoolExecutor sThreadPool = (ThreadPoolExecutor) Executors
            .newCachedThreadPool();

    /**
     * 接收其他context的结果的原始context，当前只保证主线程处理
     */
    private static WeakReference<Context> mHostContext = null;
    /**
     * context的结果处理接口
     */
    private static Runnable mContextResultRunnable = null;

    /**
     * 初始化方法
     *
     * @param app
     */
    public static void init(Application app) {
        if (sAppContext == null) {
            sAppContext = app.getApplicationContext();
        }
        if (sConnectManager == null) {
            sConnectManager = (ConnectivityManager) sAppContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        initDeviceInfo();
    }

    /**
     * 反初始化
     */
    public static void unInit() {
        sThreadPool.shutdown();
    }

    /**
     * 全局线程池
     *
     * @return
     */
    public static ThreadPoolExecutor threadPool() {
        return sThreadPool;
    }

    /**
     * 检查网络情况
     *
     * @return true:畅通，false:不畅通
     */
    public static boolean checkNetwork(Context context) {
        if (context == null) {
            return false;
        }

        if (!isNetworkAvailable(context)) {
            return false;
        }

        IMSI = ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
        return true;
    }

    /**
     * 检测SD是否存在
     *
     * @return
     */
    public static boolean checkSDCard() {

        String SDCardState = Environment.getExternalStorageState();
        RunningEnvironment.sIsValidSDCard = Environment.MEDIA_MOUNTED
                .equals(SDCardState);

        return RunningEnvironment.sIsValidSDCard;
    }

    /**
     * 检测网络是否畅通
     *
     * @return true:畅通，false:不畅通
     */
    public static boolean checkNetWork() {
        boolean isConnect = isNetworkAvailable(sAppContext);
        if (isConnect) {
            initDeviceInfo();
        }
        return isConnect;
    }

    /**
     * 初始化设备信息
     *
     * @return
     */
    public static void initDeviceInfo() {
        TelephonyManager phoneManager = (TelephonyManager) sAppContext
                .getSystemService(Context.TELEPHONY_SERVICE);

        if (TextUtils.isEmpty(PHONE_NUMBER)) {
            try {
                PHONE_NUMBER = phoneManager.getLine1Number();
            } catch (Exception e) {
            }
        }
        if (TextUtils.isEmpty(IMEI)) {
            try {
                IMEI = phoneManager.getDeviceId();
            } catch (Exception e) {
            }
        }
        if (TextUtils.isEmpty(IMSI)) {
            try {
                IMSI = phoneManager.getSubscriberId();
            } catch (Exception e) {
            }
        }

        if (TextUtils.isEmpty(MAC)) {
            try {
                WifiManager wm = (WifiManager) sAppContext
                        .getSystemService(Context.WIFI_SERVICE);
                if (wm != null) {
                    WifiInfo wi = wm.getConnectionInfo();
                    if (wi != null) {
                        String mac = wi.getMacAddress();
                        if (mac != null) {
                            MAC = mac.replace(":", "");
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取手机IMEI
     *
     * @return 设备唯一标识
     */
    public static String getIMEI() {
        if (TextUtils.isEmpty(IMEI)) {
            initDeviceInfo();
        }
        return !TextUtils.isEmpty(IMEI) ? IMEI : (!TextUtils.isEmpty(MAC) ? MAC
                : "0000000000000000");
    }

    /**
     * 获取手机IMSI
     *
     * @return sim卡唯一标识
     */
    public static String getIMSI() {
        if (TextUtils.isEmpty(IMSI)) {
            initDeviceInfo();
        }
        return IMSI != null ? IMSI : "";
    }

    /**
     * 获取系统版本
     *
     * @return
     * @author wyqiuchunlong
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    /**
     * app环境下执行装载类（改进systemClassLoader装载和appClassLoader一致）
     * 部分装载系统内部使用了systemClassLoader（例如，Serializable的装载）
     *
     * @return 装载的类
     * @throws Exception
     */
    public static <T> T callClass(Callable<T> classCaller) throws Exception {

        if (classCaller == null) {
            throw new IllegalArgumentException("classCaller must not be null.");
        }

        ClassLoader sysClassLoader = RunningEnvironment.class.getClassLoader();
        ClassLoader appClassLoader = sAppContext.getClassLoader();
        if (sysClassLoader == appClassLoader) {
            return classCaller.call();
        }

        // systemClassLoader模拟appClassLoader，调用装载类后，撤回现有分类器

        T classObj = null;
        // sysClassLoader = system + boot
        // appClassLoader = app + system + boot
        synchronized (sysClassLoader) {
            Field parentField = null;
            ClassLoader bootClassLoader = sysClassLoader.getParent();
            ClassLoader diffClassLoader = null;
            try {
                // 修改systemclassloader链式关系
                parentField = ClassLoader.class.getDeclaredField("parent");
                parentField.setAccessible(true);
                if (parentField.getType() != ClassLoader.class) {
                    parentField = null;
                } else {
                    // 查找共同的parent
                    diffClassLoader = appClassLoader;
                    while (diffClassLoader != null
                            && !sysClassLoader.equals(diffClassLoader
                            .getParent())) {
                        diffClassLoader = diffClassLoader.getParent();
                    }
                    if (diffClassLoader != null) {
                        // appClassLoader = app + boot
                        // sysClassLoader = system + appClassLoader
                        parentField.set(diffClassLoader, bootClassLoader);
                        parentField.set(sysClassLoader, appClassLoader);
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
                parentField = null;
            }

            try {
                classObj = classCaller.call();
            } finally {
                if (parentField != null) {
                    // 还原systemclassloader链式关系
                    try {
                        if (diffClassLoader != null) {
                            parentField.set(sysClassLoader, bootClassLoader);
                            parentField.set(diffClassLoader, sysClassLoader);
                        }

                        parentField.setAccessible(false);
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            }
        }

        return classObj;
    }

    /**
     * 为指定的classloader分配APP的ClassLoader
     *
     * @param src
     * @return 可能返回自身或者appclassloader
     */
    public static ClassLoader allocateAppClassLoader(ClassLoader src) {
        ClassLoader appClassLoader = sAppContext.getClassLoader();

        if (src != null) {
            // 遍历查找是否包含appclassloader
            ClassLoader cl = src;
            while (cl != null) {
                if (cl.equals(appClassLoader)) {
                    return src;
                }
                cl = cl.getParent();
            }
        }

        return appClassLoader;
    }

    /**
     * 指定的Context等待处理返回结果，通常用于非startActivityForResult处理的异常情况，
     */
    public static void startContextForResult(Context source) {
        mHostContext = null;
        mContextResultRunnable = null;

        if (source != null) {
            mHostContext = new WeakReference<Context>(source);
        }
    }

    /**
     * Context对应的结果处理方式
     */
    public static void setContextResult(Runnable result) {
        if (mHostContext != null && mHostContext.get() != null) {
            mContextResultRunnable = result;
        }
    }

    /**
     * 处理context对应的结果，被等待就结果的context调用
     *
     * @param context
     */
    public static void onContextResult(Context context) {
        if (mHostContext != null) {
            if (mHostContext.get() == context && mContextResultRunnable != null) {
                mContextResultRunnable.run();
            }
            mHostContext.clear();
            mHostContext = null;
        }

        mContextResultRunnable = null;
    }
}
