package com.cn.rpc.transport;

import com.cn.rpc.factory.RpcExampleFactory;
import com.cn.rpc.protocol.Request;
import com.cn.rpc.protocol.RpcHeader;
import com.cn.rpc.protocol.RpcMessage;
import com.cn.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Description: InvokeRunnable
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class InvokeRunnable implements Runnable {

    private ChannelHandlerContext channelHandlerContext;

    private RpcMessage<Request> requestRpcMessage;

    public InvokeRunnable(RpcMessage<Request> requestRpcMessage,
                          ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        this.requestRpcMessage = requestRpcMessage;
    }

    @Override
    public void run() {
        RpcResponse response = new RpcResponse();
        Object result = null;

        Request request = requestRpcMessage.getContent();
        String serviceName = request.getServiceName();
        Object bean = RpcExampleFactory.getBean(serviceName);
        if (Objects.isNull(bean)) {
            // todo 这里应该抛出异常
            return;
        }
        Method method = null;
        try {
            method = bean.getClass().getMethod(request.getMethodName(), request.getArgTypes());
            result = method.invoke(bean, request.getArgs());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RpcHeader header = requestRpcMessage.getHeader();
        header.setExtraInfo((byte) 1);
        response.setResult(result);
        // 这里返写给客户端
        channelHandlerContext.writeAndFlush(new RpcMessage<>(header, response));
    }
}
