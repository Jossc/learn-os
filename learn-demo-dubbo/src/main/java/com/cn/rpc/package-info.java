/**
 * @Description: package-info
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0 protocol：简易版 RPC 框架的自定义协议。
 * <p>
 * serialization：提供了自定义协议对应的序列化、反序列化的相关工具类。
 * <p>
 * codec：提供了自定义协议对应的编码器和解码器。
 * <p>
 * transport：基于 Netty 提供了底层网络通信的功能，其中会使用到 codec 包中定义编码器和解码器，以及 serialization 包中的序列化器和反序列化器。
 * <p>
 * registry：基于 ZooKeeper 和 Curator 实现了简易版本的注册中心功能。
 * <p>
 * proxy：使用 JDK 动态代理实现了一层代理。
 * <p>
 * exception : 异常类型
 */
package com.cn.rpc;