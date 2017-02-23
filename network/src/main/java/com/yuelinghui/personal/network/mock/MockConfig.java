package com.yuelinghui.personal.network.mock;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuelinghui.personal.maframe.are.RunningEnvironment;
import com.yuelinghui.personal.network.protocol.CustomProtocol;
import com.yuelinghui.personal.network.protocol.CustomProtocolGroup;
import com.yuelinghui.personal.network.protocol.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yuelinghui on 16/9/27.
 */

public class MockConfig {


    /**
     * 文件存储
     */
    private static SharedPreferences mDiskStorage = RunningEnvironment.sAppContext
            .getSharedPreferences("mock", 0);

    /**
     * Mock相关的协议
     */
    private static Map<String, MockProtocol> mockProtocols = new HashMap<String, MockProtocol>();
    /**
     * 参数控制
     */
    private static Map<String, Integer> mockParams = new HashMap<String, Integer>(
            5);
    /**
     * 模块列表
     */
    private static List<String> modules = new ArrayList<String>(5);

    /**
     * 添加模块对应Mock信息
     */
    public static synchronized void addMockConfig(String module,
                                                  MockProtocol mockProtocol, CustomProtocol protocol) {
        if (mockProtocol == null) {
            throw new IllegalArgumentException("mockProtocol must not be null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("protocol must not be null");
        }

        // 1.缺省数据
        List<Class<?>> params = CustomProtocolGroup
                .getProtocolRequestParams(protocol);
        List<Map<String, Integer>> configList = new ArrayList<Map<String, Integer>>(
                5);
        for (Class<?> param : params) {
            mockParams.put(param.getCanonicalName(), Integer.valueOf(0));
            mockProtocols.put(param.getCanonicalName(), mockProtocol);
            Map<String, Integer> map = new HashMap<String, Integer>(1);
            map.put(param.getCanonicalName(), 0);
            configList.add(map);
        }

        // 2.存储的数据
        String config = MockConfig.getString(module);
        if (!TextUtils.isEmpty(config)) {
            // 覆盖之前的configList，以本地存储的数据为主
            List<Map<String, Integer>> saveConfigList = new Gson().fromJson(
                    config, new TypeToken<List<Map<String, Integer>>>() {
                    }.getType());
            for (Map<String, Integer> saveMap : saveConfigList) {
                Map.Entry<String, Integer> entry = saveMap.entrySet().iterator()
                        .next();
                mockParams.put(entry.getKey(), entry.getValue());
                mockProtocols.put(entry.getKey(), mockProtocol);

                boolean exist = false;
                for (Map<String, Integer> map : configList) {
                    if (map.containsKey(entry.getKey())) {
                        exist = true;
                        map.put(entry.getKey(), entry.getValue());
                        break;
                    }
                }
                if (!exist) {
                    Map<String, Integer> map = new HashMap<String, Integer>(1);
                    map.put(entry.getKey(), entry.getValue());
                    configList.add(map);
                }
            }
        }

        MockConfig.putString(module, new Gson().toJson(configList));

        // 添加新模块
        for (String m : modules) {
            if (m.equals(module)) {
                return;
            }
        }
        modules.add(module);
    }

    /**
     * 添加模块对应Mock信息
     */
    public static synchronized void addMockConfig(String module, String content) {

        MockConfig.putString(module, content);

        // 添加新模块
        for (String m : modules) {
            if (m.equals(module)) {
                return;
            }
        }
        modules.add(module);
    }

    /**
     * 获取mock配置项
     *
     * @return
     */
    public static synchronized List<String> getMockConfigList() {
        List<String> list = new ArrayList<String>();
        list.addAll(modules);

        return list;
    }

    /**
     * 获取配置项内容
     */
    public static synchronized String getMockConfig(String module) {
        return MockConfig.getString(module);
    }

    /**
     * 通过配置项，初始化各种设置
     */
    public static synchronized void updateMockConfig(String module,
                                                     String content) {
        MockConfig.putString(module, content);

        updateMockParamConfig(module, content);
    }

    private static synchronized void updateMockParamConfig(String module,
                                                           String content) {
        try {
            List<HashMap<String, Integer>> configList = new Gson().fromJson(
                    content, new TypeToken<List<HashMap<String, Integer>>>() {
                    }.getType());
            for (Map<String, Integer> map : configList) {
                Iterator<Map.Entry<String, Integer>> set = map.entrySet()
                        .iterator();
                Map.Entry<String, Integer> entry = set.next();
                mockParams.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Mock情况下请求指定的结果值
     *
     * @param param
     * @return
     */
    public static MockProtocol getMockProtocol(RequestParam param) {
        String className = param.getClass().getCanonicalName();
        return mockProtocols.get(className);
    }

    /**
     * 获取Mock情况下请求指定的结果值
     *
     * @param param
     * @return
     */
    public static int getMockConfigResult(RequestParam param) {
        String className = param.getClass().getCanonicalName();
        return mockParams.get(className);
    }

    /**
     * 配置函数
     */

    /**
     * 根据键值获取数据
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return mDiskStorage.getString(key, "");
    }

    /**
     * 存储数据
     *
     * @param key
     * @param data
     */
    public static void putString(String key, String data) {
        SharedPreferences.Editor editor = mDiskStorage.edit();
        editor.putString(key, data);
        editor.commit();
    }

    /**
     * 根据键值获取数据
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return mDiskStorage.getBoolean(key, defaultValue);
    }

    /**
     * 存储数据
     *
     * @param key
     * @param data
     */
    public static void putBoolean(String key, Boolean data) {
        SharedPreferences.Editor editor = mDiskStorage.edit();
        editor.putBoolean(key, data);
        editor.commit();
    }

}
