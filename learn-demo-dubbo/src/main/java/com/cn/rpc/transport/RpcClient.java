package com.cn.rpc.transport;

import com.cn.rpc.codec.RpcDecoder;
import com.cn.rpc.codec.RpcEncoder;
import com.cn.rpc.constants.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Description: RpcClient
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class RpcClient implements Closeable {
    protected Bootstrap clientBootstrap;
    protected EventLoopGroup group;
    private String host;
    private int port;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        clientBootstrap = new Bootstrap();
        group = NettyEventLoopFactory.eventLoopGroup(Constants.DEFAULT_IO_THREADS,
                "NettyClientWorker");
        clientBootstrap.group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class) // 创建的Channel类型
                // 指定ChannelHandler的顺序
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("demo-rpc-encoder", new RpcEncoder());
                        ch.pipeline().addLast("demo-rpc-decoder", new RpcDecoder());
                        ch.pipeline().addLast("client-handler", new RpcClientHandler());
                    }
                });
    }

    public ChannelFuture connect() {
        // 连接指定的地址和端口
        ChannelFuture connect = clientBootstrap.connect(host, port);
        connect.awaitUninterruptibly();
        return connect;
    }

    @Override
    public void close() throws IOException {
        group.shutdownGracefully();
    }
}
