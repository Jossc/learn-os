package com.cn.rpc.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: RpcExampleFactory
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class RpcExampleFactory {

    private static final Map<String, Object> SERVICES = new ConcurrentHashMap<>();

    public static void registerBean(String serviceName, Object bean) {
        SERVICES.put(serviceName, bean);
    }

    public static Object getBean(String serviceName) {
        return SERVICES.get(serviceName);
    }
}
