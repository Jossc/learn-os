package com.cn.rpc.registry;

import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @Description: AbstractServiceInstanceListener
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public abstract class AbstractServiceInstanceListener<T> implements ServiceInstanceListener<T> {

    public void onFresh(ServiceInstance<T> serviceInstance, ServerInfoEvent event) {
        switch (event) {
            case ON_REGISTER:
                onRegister(serviceInstance);
                break;
            case ON_UPDATE:
                onUpdate(serviceInstance);
                break;
            case ON_REMOVE:
                onRemove(serviceInstance);
                break;
        }
    }
}
