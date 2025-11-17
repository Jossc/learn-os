package com.cn.sdk.metrics.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 指标扩展插件接口
 * 支持自定义业务指标的收集和处理逻辑
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public interface MetricsPlugin {

    /**
     * 插件名称（唯一标识）
     */
    String getName();

    /**
     * 插件版本
     */
    String getVersion();

    /**
     * 插件描述
     */
    String getDescription();

    /**
     * 插件支持的指标类型
     */
    Set<String> getSupportedMetricTypes();

    /**
     * 初始化插件
     * @param config 插件配置
     * @param context SDK上下文
     * @return 初始化是否成功
     */
    CompletableFuture<Boolean> initialize(Map<String, Object> config, SDKContext context);

    /**
     * 处理指标数据
     * @param metricData 原始指标数据
     * @return 处理后的指标数据（可以是多个）
     */
    CompletableFuture<MetricsBatch> processMetrics(MetricsData metricData);

    /**
     * 批量处理指标数据
     * @param batch 指标批次
     * @return 处理后的批次
     */
    CompletableFuture<MetricsBatch> processBatch(MetricsBatch batch);

    /**
     * 生成自定义指标
     * @param context 当前上下文
     * @return 生成的指标
     */
    CompletableFuture<MetricsBatch> generateMetrics(Map<String, Object> context);

    /**
     * 插件健康检查
     * @return 健康状态
     */
    PluginHealth getHealth();

    /**
     * 关闭插件，释放资源
     */
    CompletableFuture<Void> shutdown();

    /**
     * 获取插件统计信息
     */
    PluginStats getStats();
}

/**
 * SDK上下文接口
 */
interface SDKContext {
    SDKConfig getConfig();
    MetricsCollector getCollector();
    MetricsRegistry getRegistry();
    ExceptionHandler getExceptionHandler();

    /**
     * 获取其他插件实例
     */
    <T extends MetricsPlugin> T getPlugin(Class<T> pluginClass);

    /**
     * 发送事件到其他插件
     */
    void sendEvent(String eventType, Map<String, Object> eventData);
}

/**
 * 插件健康状态
 */
class PluginHealth {
    private final HealthStatus status;
    private final String message;
    private final long lastCheckTime;
    private final Map<String, Object> details;

    public enum HealthStatus {
        HEALTHY,
        DEGRADED,
        UNHEALTHY,
        UNKNOWN
    }

    public PluginHealth(HealthStatus status, String message, Map<String, Object> details) {
        this.status = status;
        this.message = message;
        this.lastCheckTime = System.currentTimeMillis();
        this.details = details != null ? details : new java.util.HashMap<>();
    }

    public static PluginHealth healthy() {
        return new PluginHealth(HealthStatus.HEALTHY, "Plugin is healthy", null);
    }

    public static PluginHealth degraded(String reason) {
        return new PluginHealth(HealthStatus.DEGRADED, reason, null);
    }

    public static PluginHealth unhealthy(String reason) {
        return new PluginHealth(HealthStatus.UNHEALTHY, reason, null);
    }

    // Getters
    public HealthStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public long getLastCheckTime() { return lastCheckTime; }
    public Map<String, Object> getDetails() { return new java.util.HashMap<>(details); }

    public boolean isHealthy() { return status == HealthStatus.HEALTHY; }
    public boolean isDegraded() { return status == HealthStatus.DEGRADED; }
    public boolean isUnhealthy() { return status == HealthStatus.UNHEALTHY; }
}

/**
 * 插件统计信息
 */
class PluginStats {
    private final long totalProcessedMetrics;
    private final long totalGeneratedMetrics;
    private final long totalErrors;
    private final double averageProcessingTimeMs;
    private final long memoryUsageBytes;
    private final long startTime;

    public PluginStats(long totalProcessedMetrics, long totalGeneratedMetrics, long totalErrors,
                      double averageProcessingTimeMs, long memoryUsageBytes, long startTime) {
        this.totalProcessedMetrics = totalProcessedMetrics;
        this.totalGeneratedMetrics = totalGeneratedMetrics;
        this.totalErrors = totalErrors;
        this.averageProcessingTimeMs = averageProcessingTimeMs;
        this.memoryUsageBytes = memoryUsageBytes;
        this.startTime = startTime;
    }

    // Getters
    public long getTotalProcessedMetrics() { return totalProcessedMetrics; }
    public long getTotalGeneratedMetrics() { return totalGeneratedMetrics; }
    public long getTotalErrors() { return totalErrors; }
    public double getAverageProcessingTimeMs() { return averageProcessingTimeMs; }
    public long getMemoryUsageBytes() { return memoryUsageBytes; }
    public long getStartTime() { return startTime; }
    public long getUptimeMs() { return System.currentTimeMillis() - startTime; }

    @Override
    public String toString() {
        return String.format("PluginStats{processed=%d, generated=%d, errors=%d, avgTime=%.2fms, memory=%dB, uptime=%dms}",
            totalProcessedMetrics, totalGeneratedMetrics, totalErrors,
            averageProcessingTimeMs, memoryUsageBytes, getUptimeMs());
    }
}

/**
 * 抽象插件基类
 * 提供通用的插件实现框架
 */
abstract class AbstractMetricsPlugin implements MetricsPlugin {

    protected final String name;
    protected final String version;
    protected volatile boolean initialized = false;
    protected volatile SDKContext context;
    protected volatile Map<String, Object> config;

    // 统计信息
    protected final java.util.concurrent.atomic.LongAdder processedMetrics = new java.util.concurrent.atomic.LongAdder();
    protected final java.util.concurrent.atomic.LongAdder generatedMetrics = new java.util.concurrent.atomic.LongAdder();
    protected final java.util.concurrent.atomic.LongAdder errors = new java.util.concurrent.atomic.LongAdder();
    protected final java.util.concurrent.atomic.LongAdder totalProcessingTime = new java.util.concurrent.atomic.LongAdder();
    protected final long startTime = System.currentTimeMillis();

    protected AbstractMetricsPlugin(String name, String version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public CompletableFuture<Boolean> initialize(Map<String, Object> config, SDKContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.config = config;
                this.context = context;

                // 执行具体的初始化逻辑
                boolean result = doInitialize(config, context);
                this.initialized = result;

                if (result) {
                    onInitialized();
                }

                return result;
            } catch (Exception e) {
                context.getExceptionHandler().handlePluginException(e, name);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<MetricsBatch> processMetrics(MetricsData metricData) {
        if (!initialized) {
            return CompletableFuture.completedFuture(new MetricsBatch());
        }

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                MetricsBatch result = doProcessMetrics(metricData);
                processedMetrics.increment();
                if (result != null) {
                    generatedMetrics.add(result.size());
                }
                return result != null ? result : new MetricsBatch();
            } catch (Exception e) {
                errors.increment();
                context.getExceptionHandler().handlePluginException(e, name);
                return new MetricsBatch();
            } finally {
                totalProcessingTime.add(System.currentTimeMillis() - startTime);
            }
        });
    }

    @Override
    public CompletableFuture<MetricsBatch> processBatch(MetricsBatch batch) {
        if (!initialized || batch.isEmpty()) {
            return CompletableFuture.completedFuture(batch);
        }

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                MetricsBatch result = doProcessBatch(batch);
                processedMetrics.add(batch.size());
                if (result != null) {
                    generatedMetrics.add(result.size());
                }
                return result != null ? result : batch;
            } catch (Exception e) {
                errors.increment();
                context.getExceptionHandler().handlePluginException(e, name);
                return batch;
            } finally {
                totalProcessingTime.add(System.currentTimeMillis() - startTime);
            }
        });
    }

    @Override
    public CompletableFuture<MetricsBatch> generateMetrics(Map<String, Object> context) {
        if (!initialized) {
            return CompletableFuture.completedFuture(new MetricsBatch());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                MetricsBatch result = doGenerateMetrics(context);
                if (result != null) {
                    generatedMetrics.add(result.size());
                }
                return result != null ? result : new MetricsBatch();
            } catch (Exception e) {
                errors.increment();
                this.context.getExceptionHandler().handlePluginException(e, name);
                return new MetricsBatch();
            }
        });
    }

    @Override
    public PluginHealth getHealth() {
        if (!initialized) {
            return PluginHealth.unhealthy("Plugin not initialized");
        }

        try {
            return doHealthCheck();
        } catch (Exception e) {
            return PluginHealth.unhealthy("Health check failed: " + e.getMessage());
        }
    }

    @Override
    public PluginStats getStats() {
        long processed = processedMetrics.sum();
        long generated = generatedMetrics.sum();
        long errorCount = errors.sum();
        double avgTime = processed > 0 ? (double) totalProcessingTime.sum() / processed : 0;
        long memoryUsage = getMemoryUsage();

        return new PluginStats(processed, generated, errorCount, avgTime, memoryUsage, startTime);
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            try {
                doShutdown();
                initialized = false;
            } catch (Exception e) {
                if (context != null) {
                    context.getExceptionHandler().handlePluginException(e, name);
                }
            }
        });
    }

    // 子类需要实现的抽象方法
    protected abstract boolean doInitialize(Map<String, Object> config, SDKContext context);
    protected abstract MetricsBatch doProcessMetrics(MetricsData metricData);
    protected abstract MetricsBatch doProcessBatch(MetricsBatch batch);
    protected abstract MetricsBatch doGenerateMetrics(Map<String, Object> context);
    protected abstract PluginHealth doHealthCheck();
    protected abstract void doShutdown();

    // 可选的钩子方法
    protected void onInitialized() {
        // 初始化完成后的钩子
    }

    protected long getMemoryUsage() {
        // 默认返回0，子类可以重写实现具体的内存使用统计
        return 0;
    }
}
