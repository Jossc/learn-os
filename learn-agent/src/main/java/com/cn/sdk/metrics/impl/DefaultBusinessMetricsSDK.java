package com.cn.sdk.metrics.impl;

import com.cn.sdk.metrics.core.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * BusinessMetricsSDK的默认实现
 * 整合所有核心组件，提供完整的业务指标监控功能
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class DefaultBusinessMetricsSDK implements BusinessMetricsSDK {

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    // 核心组件
    private SDKConfig config;
    private SDKStatus status;
    private MetricsCollector collector;
    private MetricsRegistry registry;
    private MetricsReporter reporter;
    private ExceptionHandler exceptionHandler;

    // 插件管理
    private final Map<String, MetricsPlugin> plugins = new ConcurrentHashMap<>();
    private final Map<Class<? extends MetricsPlugin>, MetricsPlugin> pluginsByType = new ConcurrentHashMap<>();

    // 线程池
    private ScheduledExecutorService scheduler;

    // 内部上下文实现
    private SDKContextImpl sdkContext;

    @Override
    public boolean initialize(SDKConfig config) {
        if (!initialized.compareAndSet(false, true)) {
            return false; // 已经初始化过了
        }

        try {
            this.config = config;
            this.status = new SDKStatus();
            this.status.updateState(SDKStatus.SDKState.INITIALIZING, "SDK初始化中");

            // 初始化线程池
            this.scheduler = Executors.newScheduledThreadPool(
                config.getCoreThreadPoolSize(),
                r -> {
                    Thread t = new Thread(r, "BusinessMetrics-SDK-" + System.currentTimeMillis());
                    t.setDaemon(true);
                    return t;
                }
            );

            // 初始化异常处理器
            this.exceptionHandler = new DefaultExceptionHandler();

            // 初始化核心组件
            initializeComponents();

            // 初始化插件
            initializePlugins();

            // 启动定期任务
            startScheduledTasks();

            this.status.updateState(SDKStatus.SDKState.RUNNING, "SDK初始化完成");

            // 注册关闭钩子
            registerShutdownHook();

            return true;

        } catch (Exception e) {
            this.status.updateState(SDKStatus.SDKState.ERROR, "初始化失败: " + e.getMessage());
            exceptionHandler.handleCollectionException(e, "sdk_initialization",
                Map.of("config", config.toString()));
            return false;
        }
    }

    private void initializeComponents() {
        // 初始化注册中心
        this.registry = new DefaultMetricsRegistry();
        this.status.updateComponentStatus("registry", true, "注册中心已初始化");

        // 初始化收集器
        this.collector = new DefaultMetricsCollector(config, registry, status, exceptionHandler);
        this.status.updateComponentStatus("collector", true, "收集器已初始化");

        // 初始化上报器（根据配置选择）
        this.reporter = createReporter();
        this.status.updateComponentStatus("reporter", true, "上报器已初始化");

        // 创建SDK上下文
        this.sdkContext = new SDKContextImpl();
    }

    private MetricsReporter createReporter() {
        // 这里可以根据配置创建不同类型的上报器
        if (config.isEnableSkyWalkingReporter()) {
            return new SkyWalkingMetricsReporter(config);
        } else if (config.isEnablePrometheusReporter()) {
            return new PrometheusMetricsReporter(config);
        } else if (config.isEnableElasticsearchReporter()) {
            return new ElasticsearchMetricsReporter(config);
        } else {
            return new ConsoleMetricsReporter(config);
        }
    }

    private void initializePlugins() {
        // 注册内置插件
        registerBuiltinPlugins();

        // 根据配置启用插件
        for (String pluginName : config.getEnabledPlugins()) {
            MetricsPlugin plugin = plugins.get(pluginName);
            if (plugin != null) {
                try {
                    plugin.initialize(config.getCustomProperties(), sdkContext)
                        .thenAccept(success -> {
                            if (success) {
                                status.updateComponentStatus("plugin_" + pluginName, true, "插件已启用");
                            } else {
                                status.updateComponentStatus("plugin_" + pluginName, false, "插件启用失败");
                            }
                        });
                } catch (Exception e) {
                    exceptionHandler.handlePluginException(e, pluginName);
                    status.updateComponentStatus("plugin_" + pluginName, false, "插件异常: " + e.getMessage());
                }
            }
        }
    }

    private void registerBuiltinPlugins() {
        // 订单插件
        OrderMetricsPlugin orderPlugin = new OrderMetricsPlugin();
        registerPlugin(orderPlugin);
        plugins.put("order", orderPlugin);

        // 用户插件
        UserMetricsPlugin userPlugin = new UserMetricsPlugin();
        registerPlugin(userPlugin);
        plugins.put("user", userPlugin);

        // 支付插件
        PaymentMetricsPlugin paymentPlugin = new PaymentMetricsPlugin();
        registerPlugin(paymentPlugin);
        plugins.put("payment", paymentPlugin);

        // 系统性能插件
        SystemMetricsPlugin systemPlugin = new SystemMetricsPlugin();
        registerPlugin(systemPlugin);
        plugins.put("system", systemPlugin);
    }

    private void startScheduledTasks() {
        // SDK自监控任务
        if (config.isEnableSelfMonitoring()) {
            scheduler.scheduleAtFixedRate(this::performSelfMonitoring,
                30, config.getPerformanceCheckIntervalSeconds(), TimeUnit.SECONDS);
        }

        // 指标上报任务
        scheduler.scheduleAtFixedRate(this::performMetricsReporting,
            config.getReportIntervalSeconds(), config.getReportIntervalSeconds(), TimeUnit.SECONDS);

        // 插件健康检查任务
        scheduler.scheduleAtFixedRate(this::performPluginHealthCheck,
            60, 60, TimeUnit.SECONDS);

        // 内存清理任务
        scheduler.scheduleAtFixedRate(this::performMemoryCleanup,
            300, 300, TimeUnit.SECONDS); // 每5分钟清理一次
    }

    private void performSelfMonitoring() {
        try {
            // 更新SDK性能指标
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            status.setGauge("sdk_memory_used_bytes", usedMemory);
            status.setGauge("sdk_memory_total_bytes", totalMemory);
            status.setGauge("sdk_memory_usage_percent", (double) usedMemory / totalMemory * 100);

            // 检查内存使用情况
            long memoryUsageMB = usedMemory / 1024 / 1024;
            if (memoryUsageMB > config.getMaxMemoryUsageMB()) {
                MemoryHandlingStrategy strategy = exceptionHandler.handleMemoryExhaustion(
                    memoryUsageMB, config.getMaxMemoryUsageMB());
                handleMemoryStrategy(strategy);
            }

            // 更新组件统计
            CollectorStats collectorStats = collector.getStats();
            status.setGauge("collector_buffer_usage_percent", collectorStats.getBufferUsagePercent());
            status.incrementCounter("collector_metrics_total", collectorStats.getTotalMetricsCollected());

        } catch (Exception e) {
            exceptionHandler.handleCollectionException(e, "sdk_self_monitoring", Map.of());
        }
    }

    private void performMetricsReporting() {
        try {
            // 这里可以触发收集器刷新数据到上报器
            collector.flush();
        } catch (Exception e) {
            exceptionHandler.handleCollectionException(e, "metrics_reporting", Map.of());
        }
    }

    private void performPluginHealthCheck() {
        plugins.values().forEach(plugin -> {
            try {
                PluginHealth health = plugin.getHealth();
                status.updateComponentStatus("plugin_" + plugin.getName(),
                    health.isHealthy(), health.getMessage());

                if (!health.isHealthy()) {
                    status.incrementCounter("plugin_health_check_failures");
                }
            } catch (Exception e) {
                exceptionHandler.handlePluginException(e, plugin.getName());
            }
        });
    }

    private void performMemoryCleanup() {
        try {
            // 清理过期数据
            System.gc(); // 建议垃圾回收
            status.incrementCounter("memory_cleanup_runs");
        } catch (Exception e) {
            exceptionHandler.handleCollectionException(e, "memory_cleanup", Map.of());
        }
    }

    private void handleMemoryStrategy(MemoryHandlingStrategy strategy) {
        switch (strategy) {
            case FLUSH_IMMEDIATELY:
                collector.flush();
                break;
            case DROP_OLDEST:
                // 这里需要收集器支持丢弃最旧数据的接口
                status.incrementCounter("memory_strategy_drop_oldest");
                break;
            case STOP_COLLECTION:
                status.updateState(SDKStatus.SDKState.DEGRADED, "内存不足，停止收集");
                break;
            default:
                // 其他策略的处理
                break;
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!shutdown.get()) {
                shutdown();
            }
        }, "BusinessMetrics-SDK-Shutdown"));
    }

    @Override
    public void shutdown() {
        if (!shutdown.compareAndSet(false, true)) {
            return; // 已经关闭了
        }

        status.updateState(SDKStatus.SDKState.STOPPING, "SDK关闭中");

        try {
            // 关闭插件
            plugins.values().forEach(plugin -> {
                try {
                    plugin.shutdown().get(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    exceptionHandler.handlePluginException(e, plugin.getName());
                }
            });

            // 最后一次刷新数据
            if (collector != null) {
                collector.flush().get(10, TimeUnit.SECONDS);
            }

            // 关闭上报器
            if (reporter != null) {
                reporter.shutdown().get(10, TimeUnit.SECONDS);
            }

            // 关闭线程池
            if (scheduler != null) {
                scheduler.shutdown();
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            }

            status.updateState(SDKStatus.SDKState.STOPPED, "SDK已关闭");

        } catch (Exception e) {
            status.updateState(SDKStatus.SDKState.ERROR, "关闭异常: " + e.getMessage());
        }
    }

    @Override
    public MetricsCollector getCollector() {
        return collector;
    }

    @Override
    public MetricsRegistry getRegistry() {
        return registry;
    }

    @Override
    public MetricsReporter getReporter() {
        return reporter;
    }

    @Override
    public SDKStatus getStatus() {
        return status;
    }

    @Override
    public void registerExceptionHandler(ExceptionHandler handler) {
        this.exceptionHandler = handler;
    }

    @Override
    public void registerPlugin(MetricsPlugin plugin) {
        plugins.put(plugin.getName(), plugin);
        pluginsByType.put(plugin.getClass(), plugin);
    }

    /**
     * 内部SDK上下文实现
     */
    private class SDKContextImpl implements SDKContext {
        @Override
        public SDKConfig getConfig() {
            return config;
        }

        @Override
        public MetricsCollector getCollector() {
            return collector;
        }

        @Override
        public MetricsRegistry getRegistry() {
            return registry;
        }

        @Override
        public ExceptionHandler getExceptionHandler() {
            return exceptionHandler;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends MetricsPlugin> T getPlugin(Class<T> pluginClass) {
            return (T) pluginsByType.get(pluginClass);
        }

        @Override
        public void sendEvent(String eventType, Map<String, Object> eventData) {
            // 向所有插件广播事件
            plugins.values().forEach(plugin -> {
                try {
                    // 这里可以实现更复杂的事件分发机制
                    plugin.generateMetrics(Map.of("event_type", eventType, "event_data", eventData));
                } catch (Exception e) {
                    exceptionHandler.handlePluginException(e, plugin.getName());
                }
            });
        }
    }

    /**
     * 创建SDK实例的工厂方法
     */
    public static BusinessMetricsSDK create(SDKConfig config) {
        DefaultBusinessMetricsSDK sdk = new DefaultBusinessMetricsSDK();
        if (sdk.initialize(config)) {
            return sdk;
        } else {
            throw new RuntimeException("SDK初始化失败");
        }
    }
}
