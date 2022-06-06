package com.cn.rpc.transport;

import com.cn.rpc.constants.Constants;
import com.cn.rpc.protocol.RpcMessage;
import com.cn.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

/**
 * @Description: RpcClientHandler
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcMessage<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcMessage<RpcResponse> requestRpcMessage) throws Exception {
        NettyResponseFuture<RpcResponse> responseFuture = Connection.IN_FLIGHT_REQUEST_MAP
                .remove(requestRpcMessage.getHeader().getMessageId());
        RpcResponse response = requestRpcMessage.getContent();
        // 说明这里是是心跳ping
        if (Objects.isNull(response) &&
                Constants.isHeartBeat(requestRpcMessage.getHeader().getExtraInfo())) {
            response = new RpcResponse();
            response.setCode(Constants.HEARTBEAT_CODE);
        }
        responseFuture.getPromise().setSuccess(response);
    }
}
