package com.cn.rpc.registry;


import java.util.List;

import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @Description: Registry
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public interface Registry<T> {

    void registerService(ServiceInstance<T> service) throws Exception;

    void unregisterService(ServiceInstance<T> service) throws Exception;

    List<ServiceInstance<T>> queryForInstances(String name) throws Exception;
}
