package com.cn.rpc.transport;

import com.cn.rpc.constants.Constants;
import com.cn.rpc.protocol.Request;
import com.cn.rpc.protocol.RpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Description: RpcServerHandler
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcMessage<Request>> {
    //先随便定义一个
    static Executor executor = Executors.newCachedThreadPool();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMessage<Request> requestRpcMessage) throws Exception {
        // 这里拿到附加信息
        byte extraInfo = requestRpcMessage.getHeader().getExtraInfo();
        if (Constants.isHeartBeat(extraInfo)) {
            channelHandlerContext.writeAndFlush(extraInfo);
            return;
        }
        // 直接提交到线程
        executor.execute(new InvokeRunnable(requestRpcMessage, channelHandlerContext));

    }
}
