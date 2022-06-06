package com.cn.rpc.transport;

import com.cn.rpc.protocol.RpcMessage;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * @Description: NettyResponseFuture
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
@Data
public class NettyResponseFuture<T> {

    private long createTime;
    private long timeOut;
    private RpcMessage request;
    private Channel channel;
    private Promise<T> promise;

    public NettyResponseFuture() {
    }

    public NettyResponseFuture(long createTime, long timeOut, RpcMessage request, Channel channel, Promise<T> promise) {
        this.createTime = createTime;
        this.timeOut = timeOut;
        this.request = request;
        this.channel = channel;
        this.promise = promise;
    }
}
