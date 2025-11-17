package com.cn.sdk.metrics.core;

import java.time.Instant;
import java.util.Map;

/**
 * 异常处理器接口
 * 负责处理SDK运行过程中的各种异常情况
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public interface ExceptionHandler {

    /**
     * 处理指标收集异常
     * @param exception 异常信息
     * @param metricName 指标名称
     * @param context 上下文信息
     * @return 是否继续处理后续指标
     */
    boolean handleCollectionException(Exception exception, String metricName, Map<String, Object> context);

    /**
     * 处理指标上报异常
     * @param exception 异常信息
     * @param failedMetrics 失败的指标数据
     * @return 重试策略
     */
    RetryStrategy handleReportException(Exception exception, MetricsBatch failedMetrics);

    /**
     * 处理内存不足异常
     * @param memoryUsageMB 当前内存使用量
     * @param maxMemoryMB 最大内存限制
     * @return 处理策略
     */
    MemoryHandlingStrategy handleMemoryExhaustion(long memoryUsageMB, long maxMemoryMB);

    /**
     * 处理缓冲区满异常
     * @param bufferUsed 已使用缓冲区大小
     * @param bufferSize 总缓冲区大小
     * @return 处理策略
     */
    BufferHandlingStrategy handleBufferOverflow(long bufferUsed, long bufferSize);

    /**
     * 处理网络连接异常
     * @param exception 网络异常
     * @param endpoint 目标端点
     * @return 重试策略
     */
    RetryStrategy handleNetworkException(Exception exception, String endpoint);

    /**
     * 处理序列化异常
     * @param exception 序列化异常
     * @param data 待序列化数据
     * @return 是否跳过该数据
     */
    boolean handleSerializationException(Exception exception, Object data);

    /**
     * 处理插件异常
     * @param exception 插件异常
     * @param pluginName 插件名称
     * @return 是否禁用该插件
     */
    boolean handlePluginException(Exception exception, String pluginName);
}

/**
 * 重试策略
 */
class RetryStrategy {
    private final boolean shouldRetry;
    private final int maxRetries;
    private final long retryDelayMs;
    private final boolean useExponentialBackoff;

    public RetryStrategy(boolean shouldRetry, int maxRetries, long retryDelayMs, boolean useExponentialBackoff) {
        this.shouldRetry = shouldRetry;
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
        this.useExponentialBackoff = useExponentialBackoff;
    }

    public static RetryStrategy noRetry() {
        return new RetryStrategy(false, 0, 0, false);
    }

    public static RetryStrategy retry(int maxRetries, long delayMs) {
        return new RetryStrategy(true, maxRetries, delayMs, false);
    }

    public static RetryStrategy retryWithBackoff(int maxRetries, long baseDelayMs) {
        return new RetryStrategy(true, maxRetries, baseDelayMs, true);
    }

    // Getters
    public boolean shouldRetry() { return shouldRetry; }
    public int getMaxRetries() { return maxRetries; }
    public long getRetryDelayMs() { return retryDelayMs; }
    public boolean useExponentialBackoff() { return useExponentialBackoff; }

    public long getDelayForAttempt(int attemptNumber) {
        if (!useExponentialBackoff) {
            return retryDelayMs;
        }
        return retryDelayMs * (1L << Math.min(attemptNumber, 10)); // 防止溢出
    }
}

/**
 * 内存处理策略
 */
enum MemoryHandlingStrategy {
    DROP_OLDEST,      // 丢弃最旧的数据
    DROP_NEWEST,      // 丢弃最新的数据
    COMPRESS_DATA,    // 压缩数据
    FLUSH_IMMEDIATELY, // 立即刷新到远程
    STOP_COLLECTION   // 停止收集新数据
}

/**
 * 缓冲区处理策略
 */
enum BufferHandlingStrategy {
    DROP_OLDEST,      // 丢弃最旧的数据
    DROP_CURRENT,     // 丢弃当前数据
    FLUSH_SYNC,       // 同步刷新
    FLUSH_ASYNC,      // 异步刷新
    EXPAND_BUFFER     // 扩展缓冲区（如果内存允许）
}

/**
 * 默认异常处理器实现
 */
class DefaultExceptionHandler implements ExceptionHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Override
    public boolean handleCollectionException(Exception exception, String metricName, Map<String, Object> context) {
        logger.warn("指标收集异常 - 指标: {}, 异常: {}", metricName, exception.getMessage(), exception);

        // 对于常见异常，继续处理；对于严重异常，停止处理
        if (exception instanceof OutOfMemoryError || exception instanceof StackOverflowError) {
            logger.error("严重异常，停止指标收集: {}", exception.getMessage());
            return false;
        }

        return true; // 继续处理其他指标
    }

    @Override
    public RetryStrategy handleReportException(Exception exception, MetricsBatch failedMetrics) {
        logger.warn("指标上报异常 - 批次: {}, 异常: {}", failedMetrics.getBatchId(), exception.getMessage());

        if (exception instanceof java.net.ConnectException ||
            exception instanceof java.net.SocketTimeoutException) {
            // 网络异常，使用指数退避重试
            return RetryStrategy.retryWithBackoff(3, 1000);
        } else if (exception instanceof IllegalArgumentException) {
            // 数据格式错误，不重试
            return RetryStrategy.noRetry();
        } else {
            // 其他异常，简单重试
            return RetryStrategy.retry(2, 2000);
        }
    }

    @Override
    public MemoryHandlingStrategy handleMemoryExhaustion(long memoryUsageMB, long maxMemoryMB) {
        logger.warn("内存使用超限 - 当前: {}MB, 限制: {}MB", memoryUsageMB, maxMemoryMB);

        double usagePercent = (double) memoryUsageMB / maxMemoryMB;
        if (usagePercent > 0.95) {
            return MemoryHandlingStrategy.FLUSH_IMMEDIATELY;
        } else if (usagePercent > 0.90) {
            return MemoryHandlingStrategy.DROP_OLDEST;
        } else {
            return MemoryHandlingStrategy.COMPRESS_DATA;
        }
    }

    @Override
    public BufferHandlingStrategy handleBufferOverflow(long bufferUsed, long bufferSize) {
        logger.warn("缓冲区溢出 - 已用: {}, 总计: {}", bufferUsed, bufferSize);

        double usagePercent = (double) bufferUsed / bufferSize;
        if (usagePercent > 0.98) {
            return BufferHandlingStrategy.FLUSH_SYNC;
        } else {
            return BufferHandlingStrategy.DROP_OLDEST;
        }
    }

    @Override
    public RetryStrategy handleNetworkException(Exception exception, String endpoint) {
        logger.warn("网络连接异常 - 端点: {}, 异常: {}", endpoint, exception.getMessage());

        if (exception instanceof java.net.UnknownHostException) {
            // DNS解析失败，重试几次后放弃
            return RetryStrategy.retry(2, 5000);
        } else if (exception instanceof java.net.ConnectException) {
            // 连接被拒绝，使用指数退避
            return RetryStrategy.retryWithBackoff(5, 2000);
        } else {
            return RetryStrategy.retry(3, 3000);
        }
    }

    @Override
    public boolean handleSerializationException(Exception exception, Object data) {
        logger.warn("序列化异常 - 数据类型: {}, 异常: {}",
            data != null ? data.getClass().getSimpleName() : "null", exception.getMessage());

        // 序列化失败的数据直接跳过，避免影响其他数据
        return true;
    }

    @Override
    public boolean handlePluginException(Exception exception, String pluginName) {
        logger.error("插件异常 - 插件: {}, 异常: {}", pluginName, exception.getMessage(), exception);

        // 插件异常超过3次就禁用
        return getPluginErrorCount(pluginName) < 3;
    }

    private final Map<String, Integer> pluginErrorCounts = new java.util.concurrent.ConcurrentHashMap<>();

    private int getPluginErrorCount(String pluginName) {
        return pluginErrorCounts.compute(pluginName, (k, v) -> v == null ? 1 : v + 1);
    }
}
