package com.cn.rpc.test;

import com.cn.rpc.proxy.RpcProxy;
import com.cn.rpc.registry.ServerInfo;
import com.cn.rpc.registry.ZookeeperRegistry;

/**
 * @Description: Consumer
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        // 创建ZookeeperRegistr对象
        ZookeeperRegistry<ServerInfo> discovery = new ZookeeperRegistry<>();
        discovery.start();

        // 创建代理对象，通过代理调用远端Server
        DemoService demoService = RpcProxy.newInstance(DemoService.class, discovery);
        // 调用sayHello()方法，并输出结果
        String result = demoService.sayHello("hello");
        System.out.println(result);
        // Thread.sleep(10000000L);
    }
}
