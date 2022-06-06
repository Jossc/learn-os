package com.cn.rpc.test;

import com.cn.rpc.factory.RpcExampleFactory;
import com.cn.rpc.registry.ServerInfo;
import com.cn.rpc.registry.ZookeeperRegistry;
import com.cn.rpc.transport.RpcServer;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @Description: Provider
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class Provider {
    public static void main(String[] args) throws Exception {
        // 创建DemoServiceImpl，并注册到BeanManager中
        RpcExampleFactory.registerBean("demoService", new DemoServiceImpl());
        // 创建ZookeeperRegistry，并将Provider的地址信息封装成ServerInfo
        // 对象注册到Zookeeper
        ZookeeperRegistry<ServerInfo> discovery = new ZookeeperRegistry<>();
        discovery.start();
        ServerInfo serverInfo = new ServerInfo("127.0.0.1", 20880);
        discovery.registerService(
                ServiceInstance.<ServerInfo>builder().name("demoService").payload(serverInfo).build());
        // 启动DemoRpcServer，等待Client的请求
        RpcServer rpcServer = new RpcServer(20880);
        rpcServer.start();
        Thread.sleep(100000000L);
    }
}
