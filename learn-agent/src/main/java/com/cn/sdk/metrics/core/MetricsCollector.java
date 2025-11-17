package com.cn.sdk.metrics.core;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 指标收集器接口
 * 支持同步和异步的指标收集方式
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public interface MetricsCollector {

    // === 基础指标收集 ===

    /**
     * 记录计数器指标
     * @param name 指标名称
     * @param value 增量值
     * @param tags 标签
     * @return 异步结果
     */
    CompletableFuture<Void> incrementCounter(String name, long value, Map<String, String> tags);

    /**
     * 记录计数器指标（默认增量为1）
     */
    CompletableFuture<Void> incrementCounter(String name, Map<String, String> tags);

    /**
     * 记录计量器指标
     * @param name 指标名称
     * @param value 当前值
     * @param tags 标签
     */
    CompletableFuture<Void> recordGauge(String name, double value, Map<String, String> tags);

    /**
     * 记录直方图指标
     * @param name 指标名称
     * @param value 观测值
     * @param tags 标签
     */
    CompletableFuture<Void> recordHistogram(String name, double value, Map<String, String> tags);

    /**
     * 记录计时器指标
     * @param name 指标名称
     * @param durationMs 持续时间（毫秒）
     * @param tags 标签
     */
    CompletableFuture<Void> recordTimer(String name, long durationMs, Map<String, String> tags);

    // === 业务指标收集 ===

    /**
     * 记录订单创建
     * @param orderId 订单ID
     * @param amount 订单金额
     * @param userId 用户ID
     * @param productIds 商品ID列表
     * @param metadata 额外元数据
     */
    CompletableFuture<Void> recordOrderCreated(String orderId, long amount, String userId,
                                              String[] productIds, Map<String, Object> metadata);

    /**
     * 记录订单成功
     */
    CompletableFuture<Void> recordOrderSuccess(String orderId, Map<String, Object> metadata);

    /**
     * 记录订单失败
     */
    CompletableFuture<Void> recordOrderFailed(String orderId, String failureReason,
                                             Map<String, Object> metadata);

    /**
     * 记录用户行为
     * @param userId 用户ID
     * @param action 行为类型（login, register, view, etc.）
     * @param metadata 额外信息
     */
    CompletableFuture<Void> recordUserAction(String userId, String action, Map<String, Object> metadata);

    /**
     * 记录支付事件
     */
    CompletableFuture<Void> recordPayment(String orderId, long amount, String paymentMethod,
                                         boolean success, Map<String, Object> metadata);

    // === 自定义指标收集 ===

    /**
     * 记录自定义业务事件
     * @param eventType 事件类型
     * @param eventData 事件数据
     */
    CompletableFuture<Void> recordCustomEvent(String eventType, Map<String, Object> eventData);

    /**
     * 批量记录指标
     * @param metrics 指标列表
     */
    CompletableFuture<Void> recordBatch(MetricsBatch metrics);

    // === 高级功能 ===

    /**
     * 开始计时
     * @param name 计时器名称
     * @return 计时器句柄
     */
    TimerHandle startTimer(String name, Map<String, String> tags);

    /**
     * 创建指标构建器
     * @param name 指标名称
     * @return 构建器
     */
    MetricsBuilder metric(String name);

    /**
     * 获取收集器统计信息
     */
    CollectorStats getStats();

    /**
     * 刷新缓冲区
     */
    CompletableFuture<Void> flush();
}

/**
 * 计时器句柄
 */
interface TimerHandle {
    /**
     * 停止计时并记录
     */
    CompletableFuture<Void> stop();

    /**
     * 停止计时并记录，附加额外标签
     */
    CompletableFuture<Void> stop(Map<String, String> additionalTags);

    /**
     * 获取已经过的时间（毫秒）
     */
    long elapsedMs();
}

/**
 * 指标构建器
 */
interface MetricsBuilder {
    MetricsBuilder tag(String key, String value);
    MetricsBuilder tags(Map<String, String> tags);
    CompletableFuture<Void> increment();
    CompletableFuture<Void> increment(long value);
    CompletableFuture<Void> gauge(double value);
    CompletableFuture<Void> histogram(double value);
    CompletableFuture<Void> timer(long durationMs);
}

/**
 * 收集器统计信息
 */
class CollectorStats {
    private final long totalMetricsCollected;
    private final long totalMetricsDropped;
    private final long totalErrors;
    private final double averageLatencyMs;
    private final long memoryUsageBytes;
    private final long bufferSize;
    private final long bufferUsed;

    public CollectorStats(long totalMetricsCollected, long totalMetricsDropped, long totalErrors,
                         double averageLatencyMs, long memoryUsageBytes, long bufferSize, long bufferUsed) {
        this.totalMetricsCollected = totalMetricsCollected;
        this.totalMetricsDropped = totalMetricsDropped;
        this.totalErrors = totalErrors;
        this.averageLatencyMs = averageLatencyMs;
        this.memoryUsageBytes = memoryUsageBytes;
        this.bufferSize = bufferSize;
        this.bufferUsed = bufferUsed;
    }

    // Getters
    public long getTotalMetricsCollected() { return totalMetricsCollected; }
    public long getTotalMetricsDropped() { return totalMetricsDropped; }
    public long getTotalErrors() { return totalErrors; }
    public double getAverageLatencyMs() { return averageLatencyMs; }
    public long getMemoryUsageBytes() { return memoryUsageBytes; }
    public long getBufferSize() { return bufferSize; }
    public long getBufferUsed() { return bufferUsed; }
    public double getBufferUsagePercent() { return bufferSize > 0 ? (double) bufferUsed / bufferSize * 100 : 0; }

    @Override
    public String toString() {
        return String.format("CollectorStats{collected=%d, dropped=%d, errors=%d, latency=%.2fms, memory=%dB, buffer=%d/%d(%.1f%%)}",
            totalMetricsCollected, totalMetricsDropped, totalErrors, averageLatencyMs,
            memoryUsageBytes, bufferUsed, bufferSize, getBufferUsagePercent());
    }
}
