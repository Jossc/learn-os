package com.cn.sdk.metrics.core;

/**
 * 业务指标SDK核心接口
 * 定义了所有业务指标收集的标准接口
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public interface BusinessMetricsSDK {

    /**
     * SDK版本信息
     */
    String SDK_VERSION = "2.0.0";
    String SDK_NAME = "business-metrics-sdk";

    /**
     * 初始化SDK
     * @param config SDK配置
     * @return 是否初始化成功
     */
    boolean initialize(SDKConfig config);

    /**
     * 关闭SDK，释放资源
     */
    void shutdown();

    /**
     * 获取指标收集器
     * @return 指标收集器实例
     */
    MetricsCollector getCollector();

    /**
     * 获取指标注册中心
     * @return 注册中心实例
     */
    MetricsRegistry getRegistry();

    /**
     * 获取上报器
     * @return 上报器实例
     */
    MetricsReporter getReporter();

    /**
     * 获取SDK状态
     * @return SDK状态信息
     */
    SDKStatus getStatus();

    /**
     * 注册异常处理器
     * @param handler 异常处理器
     */
    void registerExceptionHandler(ExceptionHandler handler);

    /**
     * 注册扩展插件
     * @param plugin 扩展插件
     */
    void registerPlugin(MetricsPlugin plugin);
}
