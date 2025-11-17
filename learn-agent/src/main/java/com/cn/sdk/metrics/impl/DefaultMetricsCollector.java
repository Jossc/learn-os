package com.cn.sdk.metrics.impl;

import com.cn.sdk.metrics.core.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 默认指标收集器实现
 * 提供高性能、线程安全的指标收集功能
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class DefaultMetricsCollector implements MetricsCollector {

    private final SDKConfig config;
    private final MetricsRegistry registry;
    private final SDKStatus status;
    private final ExceptionHandler exceptionHandler;

    // 异步处理
    private final ExecutorService asyncExecutor;
    private final BlockingQueue<MetricsData> buffer;
    private final ScheduledExecutorService scheduler;

    // 统计信息
    private final LongAdder totalMetricsCollected = new LongAdder();
    private final LongAdder totalMetricsDropped = new LongAdder();
    private final LongAdder totalErrors = new LongAdder();
    private final AtomicLong totalProcessingTime = new AtomicLong(0);

    // 批处理
    private final BlockingQueue<MetricsBatch> batchQueue;
    private volatile boolean running = true;

    public DefaultMetricsCollector(SDKConfig config, MetricsRegistry registry,
                                  SDKStatus status, ExceptionHandler exceptionHandler) {
        this.config = config;
        this.registry = registry;
        this.status = status;
        this.exceptionHandler = exceptionHandler;

        // 初始化线程池
        this.asyncExecutor = Executors.newFixedThreadPool(
            config.getCoreThreadPoolSize(),
            r -> new Thread(r, "MetricsCollector-Worker-" + System.currentTimeMillis())
        );

        this.scheduler = Executors.newScheduledThreadPool(2);

        // 初始化缓冲区
        this.buffer = new ArrayBlockingQueue<>(config.getBufferSize());
        this.batchQueue = new ArrayBlockingQueue<>(1000);

        // 启动后台处理任务
        startBackgroundTasks();
    }

    private void startBackgroundTasks() {
        // 批处理任务
        scheduler.scheduleAtFixedRate(this::processBatch,
            1, config.getFlushIntervalSeconds(), TimeUnit.SECONDS);

        // 缓冲区监控任务
        scheduler.scheduleAtFixedRate(this::monitorBuffer,
            10, 10, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Void> incrementCounter(String name, long value, Map<String, String> tags) {
        return CompletableFuture.runAsync(() -> {
            long startTime = System.nanoTime();
            try {
                MetricsData metric = new CounterMetric(name, value, tags, java.time.Instant.now());
                addToBuffer(metric);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, name);
            } finally {
                totalProcessingTime.addAndGet(System.nanoTime() - startTime);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> incrementCounter(String name, Map<String, String> tags) {
        return incrementCounter(name, 1L, tags);
    }

    @Override
    public CompletableFuture<Void> recordGauge(String name, double value, Map<String, String> tags) {
        return CompletableFuture.runAsync(() -> {
            long startTime = System.nanoTime();
            try {
                MetricsData metric = new GaugeMetric(name, value, tags, java.time.Instant.now());
                addToBuffer(metric);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, name);
            } finally {
                totalProcessingTime.addAndGet(System.nanoTime() - startTime);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> recordHistogram(String name, double value, Map<String, String> tags) {
        return CompletableFuture.runAsync(() -> {
            long startTime = System.nanoTime();
            try {
                MetricsData metric = new HistogramMetric(name, value, tags, java.time.Instant.now());
                addToBuffer(metric);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, name);
            } finally {
                totalProcessingTime.addAndGet(System.nanoTime() - startTime);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> recordTimer(String name, long durationMs, Map<String, String> tags) {
        return CompletableFuture.runAsync(() -> {
            long startTime = System.nanoTime();
            try {
                MetricsData metric = new TimerMetric(name, durationMs, tags, java.time.Instant.now());
                addToBuffer(metric);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, name);
            } finally {
                totalProcessingTime.addAndGet(System.nanoTime() - startTime);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> recordOrderCreated(String orderId, long amount, String userId,
                                                     String[] productIds, Map<String, Object> metadata) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> eventData = new java.util.HashMap<>(metadata != null ? metadata : new java.util.HashMap<>());
                eventData.put("order_id", orderId);
                eventData.put("amount", amount);
                eventData.put("user_id", userId);
                eventData.put("product_ids", productIds);

                Map<String, String> tags = Map.of(
                    "event_type", "order_created",
                    "user_id", userId != null ? userId : "unknown",
                    "order_id", orderId
                );

                BusinessEventMetric event = new BusinessEventMetric("order_created", eventData, tags, java.time.Instant.now());
                addToBuffer(event);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, "order_created");
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> recordOrderSuccess(String orderId, Map<String, Object> metadata) {
        return recordOrderEvent("order_success", orderId, metadata);
    }

    @Override
    public CompletableFuture<Void> recordOrderFailed(String orderId, String failureReason, Map<String, Object> metadata) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> eventData = new java.util.HashMap<>(metadata != null ? metadata : new java.util.HashMap<>());
                eventData.put("order_id", orderId);
                eventData.put("failure_reason", failureReason);

                Map<String, String> tags = Map.of(
                    "event_type", "order_failed",
                    "order_id", orderId,
                    "failure_reason", failureReason != null ? failureReason : "unknown"
                );

                BusinessEventMetric event = new BusinessEventMetric("order_failed", eventData, tags, java.time.Instant.now());
                addToBuffer(event);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, "order_failed");
            }
        }, asyncExecutor);
    }

    private CompletableFuture<Void> recordOrderEvent(String eventType, String orderId, Map<String, Object> metadata) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> eventData = new java.util.HashMap<>(metadata != null ? metadata : new java.util.HashMap<>());
                eventData.put("order_id", orderId);

                Map<String, String> tags = Map.of(
                    "event_type", eventType,
                    "order_id", orderId
                );

                BusinessEventMetric event = new BusinessEventMetric(eventType, eventData, tags, java.time.Instant.now());
                addToBuffer(event);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, eventType);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> recordUserAction(String userId, String action, Map<String, Object> metadata) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> eventData = new java.util.HashMap<>(metadata != null ? metadata : new java.util.HashMap<>());
                eventData.put("user_id", userId);
                eventData.put("action", action);

                Map<String, String> tags = Map.of(
                    "event_type", "user_action",
                    "user_id", userId != null ? userId : "unknown",
                    "action", action
                );

                BusinessEventMetric event = new BusinessEventMetric("user_action", eventData, tags, java.time.Instant.now());
                addToBuffer(event);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, "user_action");
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> recordPayment(String orderId, long amount, String paymentMethod,
                                                boolean success, Map<String, Object> metadata) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> eventData = new java.util.HashMap<>(metadata != null ? metadata : new java.util.HashMap<>());
                eventData.put("order_id", orderId);
                eventData.put("amount", amount);
                eventData.put("payment_method", paymentMethod);
                eventData.put("success", success);

                Map<String, String> tags = Map.of(
                    "event_type", "payment",
                    "order_id", orderId,
                    "payment_method", paymentMethod != null ? paymentMethod : "unknown",
                    "success", String.valueOf(success)
                );

                BusinessEventMetric event = new BusinessEventMetric("payment", eventData, tags, java.time.Instant.now());
                addToBuffer(event);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, "payment");
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> recordCustomEvent(String eventType, Map<String, Object> eventData) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> tags = Map.of(
                    "event_type", "custom",
                    "custom_event_type", eventType
                );

                BusinessEventMetric event = new BusinessEventMetric(eventType, eventData, tags, java.time.Instant.now());
                addToBuffer(event);
                totalMetricsCollected.increment();
            } catch (Exception e) {
                handleCollectionError(e, eventType);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> recordBatch(MetricsBatch metrics) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!batchQueue.offer(metrics)) {
                    // 队列满了，处理策略
                    BufferHandlingStrategy strategy = exceptionHandler.handleBufferOverflow(
                        batchQueue.size(), 1000);
                    handleBufferStrategy(strategy);

                    // 重试一次
                    if (!batchQueue.offer(metrics)) {
                        totalMetricsDropped.add(metrics.size());
                    }
                } else {
                    totalMetricsCollected.add(metrics.size());
                }
            } catch (Exception e) {
                handleCollectionError(e, "batch_record");
            }
        }, asyncExecutor);
    }

    @Override
    public TimerHandle startTimer(String name, Map<String, String> tags) {
        return new DefaultTimerHandle(name, tags, this);
    }

    @Override
    public MetricsBuilder metric(String name) {
        return new DefaultMetricsBuilder(name, this);
    }

    @Override
    public CollectorStats getStats() {
        long collected = totalMetricsCollected.sum();
        long dropped = totalMetricsDropped.sum();
        long errors = totalErrors.sum();
        double avgLatency = collected > 0 ? (double) totalProcessingTime.get() / collected / 1_000_000 : 0; // 转换为毫秒

        Runtime runtime = Runtime.getRuntime();
        long memoryUsage = runtime.totalMemory() - runtime.freeMemory();

        return new CollectorStats(collected, dropped, errors, avgLatency, memoryUsage,
            config.getBufferSize(), buffer.size());
    }

    @Override
    public CompletableFuture<Void> flush() {
        return CompletableFuture.runAsync(() -> {
            try {
                processBatch();
            } catch (Exception e) {
                exceptionHandler.handleCollectionException(e, "flush", Map.of());
            }
        }, asyncExecutor);
    }

    private void addToBuffer(MetricsData metric) {
        // 验证指标
        if (registry != null) {
            ValidationResult validation = registry.validateMetric(metric.getName(), metric);
            if (!validation.isValid()) {
                totalErrors.increment();
                return;
            }
        }

        if (!buffer.offer(metric)) {
            // 缓冲区满了，处理策略
            BufferHandlingStrategy strategy = exceptionHandler.handleBufferOverflow(
                buffer.size(), config.getBufferSize());
            handleBufferStrategy(strategy);

            // 重试添加
            if (!buffer.offer(metric)) {
                totalMetricsDropped.increment();
            }
        }
    }

    private void handleBufferStrategy(BufferHandlingStrategy strategy) {
        switch (strategy) {
            case DROP_OLDEST:
                buffer.poll(); // 删除最旧的数据
                break;
            case FLUSH_ASYNC:
                CompletableFuture.runAsync(this::processBatch, asyncExecutor);
                break;
            case FLUSH_SYNC:
                processBatch();
                break;
            default:
                // 其他策略处理
                break;
        }
    }

    private void processBatch() {
        if (!running) return;

        try {
            // 从缓冲区收集数据
            MetricsBatch batch = new MetricsBatch();
            MetricsData metric;
            int count = 0;

            while ((metric = buffer.poll()) != null && count < config.getMaxBatchSize()) {
                batch.getMetrics().add(metric);
                count++;
            }

            // 处理批次队列中的数据
            MetricsBatch batchFromQueue;
            while ((batchFromQueue = batchQueue.poll()) != null && count < config.getMaxBatchSize()) {
                batch.getMetrics().addAll(batchFromQueue.getMetrics());
                count += batchFromQueue.size();
            }

            if (!batch.isEmpty()) {
                // 这里应该将批次发送给上报器
                // 由于上报器还未实现，暂时记录日志
                System.out.println("📊 批处理指标: " + batch.size() + "个指标");
            }

        } catch (Exception e) {
            exceptionHandler.handleCollectionException(e, "batch_processing", Map.of());
        }
    }

    private void monitorBuffer() {
        try {
            double bufferUsage = (double) buffer.size() / config.getBufferSize() * 100;
            status.setGauge("collector_buffer_usage_percent", bufferUsage);

            if (bufferUsage > 80) {
                status.updateComponentStatus("collector", false,
                    String.format("缓冲区使用率过高: %.1f%%", bufferUsage));
            } else {
                status.updateComponentStatus("collector", true, "收集器运行正常");
            }
        } catch (Exception e) {
            exceptionHandler.handleCollectionException(e, "buffer_monitoring", Map.of());
        }
    }

    private void handleCollectionError(Exception e, String metricName) {
        totalErrors.increment();
        boolean shouldContinue = exceptionHandler.handleCollectionException(e, metricName,
            Map.of("collector", "default"));
        if (!shouldContinue) {
            running = false;
        }
    }

    public void shutdown() {
        running = false;

        try {
            // 最后一次刷新
            processBatch();

            // 关闭线程池
            scheduler.shutdown();
            asyncExecutor.shutdown();

            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/**
 * 默认计时器句柄实现
 */
class DefaultTimerHandle implements TimerHandle {
    private final String name;
    private final Map<String, String> tags;
    private final MetricsCollector collector;
    private final long startTime;

    public DefaultTimerHandle(String name, Map<String, String> tags, MetricsCollector collector) {
        this.name = name;
        this.tags = new java.util.HashMap<>(tags);
        this.collector = collector;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public CompletableFuture<Void> stop() {
        return stop(new java.util.HashMap<>());
    }

    @Override
    public CompletableFuture<Void> stop(Map<String, String> additionalTags) {
        long duration = elapsedMs();
        Map<String, String> allTags = new java.util.HashMap<>(tags);
        allTags.putAll(additionalTags);
        return collector.recordTimer(name, duration, allTags);
    }

    @Override
    public long elapsedMs() {
        return System.currentTimeMillis() - startTime;
    }
}

/**
 * 默认指标构建器实现
 */
class DefaultMetricsBuilder implements MetricsBuilder {
    private final String name;
    private final MetricsCollector collector;
    private final Map<String, String> tags = new java.util.HashMap<>();

    public DefaultMetricsBuilder(String name, MetricsCollector collector) {
        this.name = name;
        this.collector = collector;
    }

    @Override
    public MetricsBuilder tag(String key, String value) {
        tags.put(key, value);
        return this;
    }

    @Override
    public MetricsBuilder tags(Map<String, String> tags) {
        this.tags.putAll(tags);
        return this;
    }

    @Override
    public CompletableFuture<Void> increment() {
        return collector.incrementCounter(name, tags);
    }

    @Override
    public CompletableFuture<Void> increment(long value) {
        return collector.incrementCounter(name, value, tags);
    }

    @Override
    public CompletableFuture<Void> gauge(double value) {
        return collector.recordGauge(name, value, tags);
    }

    @Override
    public CompletableFuture<Void> histogram(double value) {
        return collector.recordHistogram(name, value, tags);
    }

    @Override
    public CompletableFuture<Void> timer(long durationMs) {
        return collector.recordTimer(name, durationMs, tags);
    }
}
