package com.cn.sdk.metrics.core;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SDK配置类
 * 支持多种配置方式：代码配置、文件配置、环境变量配置
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class SDKConfig {

    // === 基础配置 ===
    private String serviceName = "unknown-service";
    private String version = "1.0.0";
    private String environment = "production";
    private boolean enabled = true;

    // === 性能配置 ===
    private int maxThreadPoolSize = 5;
    private int coreThreadPoolSize = 2;
    private long reportIntervalSeconds = 30;
    private int maxBatchSize = 1000;
    private long maxMemoryUsageMB = 100;

    // === 缓冲配置 ===
    private int bufferSize = 10000;
    private long flushIntervalSeconds = 10;
    private boolean enableLocalCache = true;
    private String cacheDirectory = "./metrics-cache";

    // === 上报配置 ===
    private boolean enableSkyWalkingReporter = true;
    private boolean enablePrometheusReporter = false;
    private boolean enableElasticsearchReporter = false;
    private boolean enableConsoleReporter = false;

    // SkyWalking配置
    private String skyWalkingOapServer = "127.0.0.1:11800";
    private int skyWalkingConnectionTimeout = 5000;
    private int skyWalkingRetryTimes = 3;

    // Prometheus配置
    private int prometheusPort = 9090;
    private String prometheusPath = "/metrics";

    // Elasticsearch配置
    private String elasticsearchHosts = "127.0.0.1:9200";
    private String elasticsearchIndex = "business-metrics";

    // === 异常处理配置 ===
    private boolean enableExceptionRecovery = true;
    private int maxRetryTimes = 3;
    private long retryIntervalSeconds = 5;
    private boolean enableCircuitBreaker = true;
    private int circuitBreakerThreshold = 10;

    // === 扩展配置 ===
    private Map<String, Object> customProperties;
    private String[] enabledPlugins = {"order", "user", "payment"};
    private String[] disabledPlugins = {};

    // === 监控配置 ===
    private boolean enableSelfMonitoring = true;
    private boolean enablePerformanceMonitoring = true;
    private long performanceCheckIntervalSeconds = 60;

    // === 安全配置 ===
    private boolean enableEncryption = false;
    private String encryptionKey;
    private boolean enableAuthentication = false;
    private String authToken;

    // Builder模式构建配置
    public static class Builder {
        private SDKConfig config = new SDKConfig();

        public Builder serviceName(String serviceName) {
            config.serviceName = serviceName;
            return this;
        }

        public Builder version(String version) {
            config.version = version;
            return this;
        }

        public Builder environment(String environment) {
            config.environment = environment;
            return this;
        }

        public Builder enabled(boolean enabled) {
            config.enabled = enabled;
            return this;
        }

        public Builder reportInterval(long seconds) {
            config.reportIntervalSeconds = seconds;
            return this;
        }

        public Builder maxBatchSize(int size) {
            config.maxBatchSize = size;
            return this;
        }

        public Builder bufferSize(int size) {
            config.bufferSize = size;
            return this;
        }

        public Builder skyWalkingOapServer(String server) {
            config.skyWalkingOapServer = server;
            return this;
        }

        public Builder enableSkyWalking(boolean enable) {
            config.enableSkyWalkingReporter = enable;
            return this;
        }

        public Builder enablePrometheus(boolean enable, int port) {
            config.enablePrometheusReporter = enable;
            config.prometheusPort = port;
            return this;
        }

        public Builder enableElasticsearch(boolean enable, String hosts) {
            config.enableElasticsearchReporter = enable;
            config.elasticsearchHosts = hosts;
            return this;
        }

        public Builder enableConsole(boolean enable) {
            config.enableConsoleReporter = enable;
            return this;
        }

        public Builder customProperty(String key, Object value) {
            if (config.customProperties == null) {
                config.customProperties = new java.util.HashMap<>();
            }
            config.customProperties.put(key, value);
            return this;
        }

        public Builder enabledPlugins(String... plugins) {
            config.enabledPlugins = plugins;
            return this;
        }

        public Builder maxMemoryUsage(long mb) {
            config.maxMemoryUsageMB = mb;
            return this;
        }

        public Builder enableSelfMonitoring(boolean enable) {
            config.enableSelfMonitoring = enable;
            return this;
        }

        public SDKConfig build() {
            // 配置验证
            if (config.serviceName == null || config.serviceName.trim().isEmpty()) {
                throw new IllegalArgumentException("服务名称不能为空");
            }
            if (config.reportIntervalSeconds <= 0) {
                throw new IllegalArgumentException("上报间隔必须大于0");
            }
            if (config.maxBatchSize <= 0) {
                throw new IllegalArgumentException("批处理大小必须大于0");
            }

            return config;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // === Getters ===
    public String getServiceName() { return serviceName; }
    public String getVersion() { return version; }
    public String getEnvironment() { return environment; }
    public boolean isEnabled() { return enabled; }
    public int getMaxThreadPoolSize() { return maxThreadPoolSize; }
    public int getCoreThreadPoolSize() { return coreThreadPoolSize; }
    public long getReportIntervalSeconds() { return reportIntervalSeconds; }
    public int getMaxBatchSize() { return maxBatchSize; }
    public long getMaxMemoryUsageMB() { return maxMemoryUsageMB; }
    public int getBufferSize() { return bufferSize; }
    public long getFlushIntervalSeconds() { return flushIntervalSeconds; }
    public boolean isEnableLocalCache() { return enableLocalCache; }
    public String getCacheDirectory() { return cacheDirectory; }
    public boolean isEnableSkyWalkingReporter() { return enableSkyWalkingReporter; }
    public boolean isEnablePrometheusReporter() { return enablePrometheusReporter; }
    public boolean isEnableElasticsearchReporter() { return enableElasticsearchReporter; }
    public boolean isEnableConsoleReporter() { return enableConsoleReporter; }
    public String getSkyWalkingOapServer() { return skyWalkingOapServer; }
    public int getSkyWalkingConnectionTimeout() { return skyWalkingConnectionTimeout; }
    public int getSkyWalkingRetryTimes() { return skyWalkingRetryTimes; }
    public int getPrometheusPort() { return prometheusPort; }
    public String getPrometheusPath() { return prometheusPath; }
    public String getElasticsearchHosts() { return elasticsearchHosts; }
    public String getElasticsearchIndex() { return elasticsearchIndex; }
    public boolean isEnableExceptionRecovery() { return enableExceptionRecovery; }
    public int getMaxRetryTimes() { return maxRetryTimes; }
    public long getRetryIntervalSeconds() { return retryIntervalSeconds; }
    public boolean isEnableCircuitBreaker() { return enableCircuitBreaker; }
    public int getCircuitBreakerThreshold() { return circuitBreakerThreshold; }
    public Map<String, Object> getCustomProperties() { return customProperties; }
    public String[] getEnabledPlugins() { return enabledPlugins; }
    public String[] getDisabledPlugins() { return disabledPlugins; }
    public boolean isEnableSelfMonitoring() { return enableSelfMonitoring; }
    public boolean isEnablePerformanceMonitoring() { return enablePerformanceMonitoring; }
    public long getPerformanceCheckIntervalSeconds() { return performanceCheckIntervalSeconds; }
    public boolean isEnableEncryption() { return enableEncryption; }
    public String getEncryptionKey() { return encryptionKey; }
    public boolean isEnableAuthentication() { return enableAuthentication; }
    public String getAuthToken() { return authToken; }

    @Override
    public String toString() {
        return String.format("SDKConfig{service='%s', version='%s', env='%s', enabled=%s, reportInterval=%ds}",
            serviceName, version, environment, enabled, reportIntervalSeconds);
    }
}
