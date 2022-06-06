package com.cn.rpc.transport;

import com.cn.rpc.codec.RpcDecoder;
import com.cn.rpc.codec.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Description: RpcServer
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class RpcServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private Channel channel;
    protected int port;

    public RpcServer(int port) throws InterruptedException {
        this.port = port;
        // 创建boss和worker两个EventLoopGroup，注意一些小细节，
        // workerGroup 是按照中的线程数是按照 CPU 核数计算得到的
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1,
                "NettyServerBoss");
        workerGroup = NettyEventLoopFactory.eventLoopGroup(
                Math.min(Runtime.getRuntime().availableProcessors() + 1, 32),
                "NettyServerWorker");
        serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // 指定每个Channel上注册的ChannelHandler以及顺序
                .handler(new LoggingHandler(LogLevel.INFO)).childHandler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast("demp-rpc-decoder", new RpcDecoder());
                                ch.pipeline().addLast("demo-rpc-encoder", new RpcEncoder());
                                ch.pipeline().addLast("server-handler", new RpcServerHandler());
                            }
                        });
    }

    public ChannelFuture start() throws InterruptedException {
        // 监听指定的端口
        ChannelFuture channelFuture = serverBootstrap.bind(port);
        channel = channelFuture.channel();
        channel.closeFuture();
        return channelFuture;
    }


    public void startAndWait() throws InterruptedException {
        try {
            channel.closeFuture().await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }


    public void shutdown() throws InterruptedException {
        channel.close().sync();
        if (bossGroup != null)
            bossGroup.shutdownGracefully().awaitUninterruptibly(15000);
        if (workerGroup != null)
            workerGroup.shutdownGracefully().awaitUninterruptibly(15000);
    }
}
