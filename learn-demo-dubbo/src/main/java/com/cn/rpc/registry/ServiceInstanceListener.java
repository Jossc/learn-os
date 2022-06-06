package com.cn.rpc.registry;

import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @Description: ServiceInstanceListener
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public interface ServiceInstanceListener<T> {

    void onRegister(ServiceInstance<T> serviceInstance);

    void onRemove(ServiceInstance<T> serviceInstance);

    void onUpdate(ServiceInstance<T> serviceInstance);

    void onFresh(ServiceInstance<T> serviceInstance, ServerInfoEvent event);

    enum ServerInfoEvent {
        ON_REGISTER,
        ON_UPDATE,
        ON_REMOVE
    }
}
