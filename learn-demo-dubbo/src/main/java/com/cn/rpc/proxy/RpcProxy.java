package com.cn.rpc.proxy;

import com.cn.rpc.constants.Constants;
import com.cn.rpc.protocol.Request;
import com.cn.rpc.protocol.RpcHeader;
import com.cn.rpc.protocol.RpcMessage;
import com.cn.rpc.protocol.RpcResponse;
import com.cn.rpc.registry.Registry;
import com.cn.rpc.registry.ServerInfo;
import com.cn.rpc.transport.Connection;
import com.cn.rpc.transport.NettyResponseFuture;
import com.cn.rpc.transport.RpcClient;
import io.netty.channel.ChannelFuture;
import org.apache.curator.x.discovery.ServiceInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.cn.rpc.constants.Constants.MAGIC;
import static com.cn.rpc.constants.Constants.VERSION_1;

/**
 * @Description: RpcProxy
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class RpcProxy implements InvocationHandler {
    // 需要代理的服务(接口)名称
    private String serviceName;

    public Map<Method, RpcHeader> headerCache = new ConcurrentHashMap<>();

    // 用于与Zookeeper交互，其中自带缓存
    private Registry<ServerInfo> registry;

    public RpcProxy(String serviceName, Registry<ServerInfo> registry) throws Exception {
        this.serviceName = serviceName;
        this.registry = registry;
    }

    public static <T> T newInstance(Class<T> clazz, Registry<ServerInfo> registry) throws Exception {
        // 创建代理对象
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new RpcProxy("demoService", registry));
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 从Zookeeper缓存中获取可用的Server地址,并随机从中选择一个
        List<ServiceInstance<ServerInfo>> serviceInstances =
                registry.queryForInstances(serviceName);
        ServiceInstance<ServerInfo> serviceInstance =
                serviceInstances.get(ThreadLocalRandom.current().nextInt(serviceInstances.size()));
        // 创建请求消息，然后调用remoteCall()方法请求上面选定的Server端
        String methodName = method.getName();
        RpcHeader header = headerCache.computeIfAbsent(method, h -> new RpcHeader(MAGIC, VERSION_1));
        RpcMessage<Request> message = new RpcMessage(header, new Request(serviceName, methodName, args));
        return remoteCall(serviceInstance.getPayload(), message);
    }

    protected Object remoteCall(ServerInfo serverInfo, RpcMessage message) throws Exception {
        if (serverInfo == null) {
            throw new RuntimeException("get available server error");
        }
        Object result;
        try {
            // 创建RpcClient连接指定的Server端
            RpcClient demoRpcClient = new RpcClient(serverInfo.getHost(), serverInfo.getPort());
            ChannelFuture channelFuture = demoRpcClient.connect().awaitUninterruptibly();
            // 创建对应的Connection对象，并发送请求
            Connection connection = new Connection(channelFuture, true);
            NettyResponseFuture<RpcResponse> responseFuture = connection.request(message, Constants.DEFAULT_TIMEOUT);
            // 等待请求对应的响应
            //result = responseFuture.getPromise().get(3, TimeUnit.MILLISECONDS);
            result = responseFuture.getPromise().get(Constants.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            throw e;
        }
        return result;
    }
}
