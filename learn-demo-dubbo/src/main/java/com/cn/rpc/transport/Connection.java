package com.cn.rpc.transport;

import com.cn.rpc.constants.Constants;
import com.cn.rpc.protocol.RpcHeader;
import com.cn.rpc.protocol.RpcMessage;
import com.cn.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: Connection
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class Connection implements Closeable {

    // 用于生成消息ID，全局唯一
    private final static AtomicLong ID_GENERATOR = new AtomicLong(0);

    // TODO 时间轮定时删除
    public final static Map<Long, NettyResponseFuture<RpcResponse>> IN_FLIGHT_REQUEST_MAP
            = new ConcurrentHashMap<>();

    private ChannelFuture future;

    private AtomicBoolean isConnected = new AtomicBoolean();

    public Connection() {
        this.isConnected.set(false);
        this.future = null;
    }

    public Connection(ChannelFuture future, boolean isConnected) {
        this.future = future;
        this.isConnected.set(isConnected);
    }


    public NettyResponseFuture<RpcResponse> request(RpcMessage message, long timeOut) {
        // 生成并设置消息ID
        long messageId = ID_GENERATOR.incrementAndGet();
        message.getHeader().setMessageId(messageId);
        // 创建消息关联的Future
        NettyResponseFuture responseFuture = new NettyResponseFuture(System.currentTimeMillis(),
                timeOut, message, future.channel(), new DefaultPromise(new DefaultEventLoop()));
        // 将消息ID和关联的Future记录到IN_FLIGHT_REQUEST_MAP集合中
        IN_FLIGHT_REQUEST_MAP.put(messageId, responseFuture);
        try {
            future.channel().writeAndFlush(message); // 发送请求
        } catch (Exception e) {
            // 发送请求异常时，删除对应的Future
            IN_FLIGHT_REQUEST_MAP.remove(messageId);
            throw e;
        }
        return responseFuture;
    }

    /**
     * ping
     * 后边需要添加续约断链
     *
     * @return
     */
    public boolean ping() {
        RpcHeader header = new RpcHeader(Constants.MAGIC, Constants.VERSION_1);
        header.setExtraInfo(Constants.HEART_EXTRA_INFO);
        RpcMessage message = new RpcMessage(header, null);
        NettyResponseFuture<RpcResponse> request = request(message, Constants.DEFAULT_TIMEOUT);
        try {
            Promise<RpcResponse> await = request.getPromise().await();
            return await.get().getCode() == Constants.HEARTBEAT_CODE;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        future.channel().close();
    }


}