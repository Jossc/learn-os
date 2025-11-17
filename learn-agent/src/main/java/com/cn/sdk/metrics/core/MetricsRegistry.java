package com.cn.sdk.metrics.core;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 指标注册中心接口
 * 管理所有指标定义、元数据和生命周期
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public interface MetricsRegistry {

    /**
     * 注册指标定义
     * @param definition 指标定义
     * @return 注册是否成功
     */
    boolean registerMetric(MetricDefinition definition);

    /**
     * 批量注册指标
     * @param definitions 指标定义列表
     * @return 成功注册的数量
     */
    int registerMetrics(List<MetricDefinition> definitions);

    /**
     * 注销指标
     * @param metricName 指标名称
     * @return 注销是否成功
     */
    boolean unregisterMetric(String metricName);

    /**
     * 获取指标定义
     * @param metricName 指标名称
     * @return 指标定义，如果不存在返回null
     */
    MetricDefinition getMetricDefinition(String metricName);

    /**
     * 获取所有已注册的指标名称
     */
    Set<String> getAllMetricNames();

    /**
     * 按类型获取指标定义
     * @param metricType 指标类型
     */
    List<MetricDefinition> getMetricsByType(MetricType metricType);

    /**
     * 按标签过滤指标
     * @param tagFilters 标签过滤条件
     */
    List<MetricDefinition> getMetricsByTags(Map<String, String> tagFilters);

    /**
     * 验证指标数据是否符合定义
     * @param metricName 指标名称
     * @param data 指标数据
     * @return 验证结果
     */
    ValidationResult validateMetric(String metricName, MetricsData data);

    /**
     * 获取注册中心统计信息
     */
    RegistryStats getStats();
}

/**
 * 指标定义
 */
class MetricDefinition {
    private final String name;
    private final MetricType type;
    private final String description;
    private final String unit;
    private final Map<String, String> defaultTags;
    private final Map<String, Object> metadata;
    private final ValidationRules validationRules;
    private final long creationTime;

    public MetricDefinition(String name, MetricType type, String description, String unit,
                           Map<String, String> defaultTags, Map<String, Object> metadata,
                           ValidationRules validationRules) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.unit = unit;
        this.defaultTags = defaultTags != null ? new java.util.HashMap<>(defaultTags) : new java.util.HashMap<>();
        this.metadata = metadata != null ? new java.util.HashMap<>(metadata) : new java.util.HashMap<>();
        this.validationRules = validationRules != null ? validationRules : new ValidationRules();
        this.creationTime = System.currentTimeMillis();
    }

    // Builder模式
    public static class Builder {
        private String name;
        private MetricType type;
        private String description = "";
        private String unit = "";
        private Map<String, String> defaultTags = new java.util.HashMap<>();
        private Map<String, Object> metadata = new java.util.HashMap<>();
        private ValidationRules validationRules = new ValidationRules();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(MetricType type) {
            this.type = type;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder unit(String unit) {
            this.unit = unit;
            return this;
        }

        public Builder defaultTag(String key, String value) {
            this.defaultTags.put(key, value);
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder validationRules(ValidationRules rules) {
            this.validationRules = rules;
            return this;
        }

        public MetricDefinition build() {
            if (name == null || type == null) {
                throw new IllegalArgumentException("指标名称和类型不能为空");
            }
            return new MetricDefinition(name, type, description, unit, defaultTags, metadata, validationRules);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getName() { return name; }
    public MetricType getType() { return type; }
    public String getDescription() { return description; }
    public String getUnit() { return unit; }
    public Map<String, String> getDefaultTags() { return new java.util.HashMap<>(defaultTags); }
    public Map<String, Object> getMetadata() { return new java.util.HashMap<>(metadata); }
    public ValidationRules getValidationRules() { return validationRules; }
    public long getCreationTime() { return creationTime; }
}

/**
 * 验证规则
 */
class ValidationRules {
    private Double minValue;
    private Double maxValue;
    private Set<String> allowedStringValues;
    private Set<String> requiredTags;
    private Set<String> allowedTags;
    private boolean allowNullValues = false;

    public ValidationRules minValue(double min) {
        this.minValue = min;
        return this;
    }

    public ValidationRules maxValue(double max) {
        this.maxValue = max;
        return this;
    }

    public ValidationRules allowedValues(String... values) {
        this.allowedStringValues = Set.of(values);
        return this;
    }

    public ValidationRules requiredTags(String... tags) {
        this.requiredTags = Set.of(tags);
        return this;
    }

    public ValidationRules allowedTags(String... tags) {
        this.allowedTags = Set.of(tags);
        return this;
    }

    public ValidationRules allowNullValues(boolean allow) {
        this.allowNullValues = allow;
        return this;
    }

    // Getters
    public Double getMinValue() { return minValue; }
    public Double getMaxValue() { return maxValue; }
    public Set<String> getAllowedStringValues() { return allowedStringValues; }
    public Set<String> getRequiredTags() { return requiredTags; }
    public Set<String> getAllowedTags() { return allowedTags; }
    public boolean isAllowNullValues() { return allowNullValues; }
}

/**
 * 验证结果
 */
class ValidationResult {
    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;

    public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
        this.valid = valid;
        this.errors = errors != null ? errors : new java.util.ArrayList<>();
        this.warnings = warnings != null ? warnings : new java.util.ArrayList<>();
    }

    public static ValidationResult success() {
        return new ValidationResult(true, null, null);
    }

    public static ValidationResult failure(String... errors) {
        return new ValidationResult(false, List.of(errors), null);
    }

    public static ValidationResult warning(String... warnings) {
        return new ValidationResult(true, null, List.of(warnings));
    }

    // Getters
    public boolean isValid() { return valid; }
    public List<String> getErrors() { return new java.util.ArrayList<>(errors); }
    public List<String> getWarnings() { return new java.util.ArrayList<>(warnings); }
    public boolean hasErrors() { return !errors.isEmpty(); }
    public boolean hasWarnings() { return !warnings.isEmpty(); }
}

/**
 * 注册中心统计信息
 */
class RegistryStats {
    private final int totalRegisteredMetrics;
    private final Map<MetricType, Integer> metricsByType;
    private final long totalValidations;
    private final long successfulValidations;
    private final long failedValidations;
    private final double averageValidationTimeMs;

    public RegistryStats(int totalRegisteredMetrics, Map<MetricType, Integer> metricsByType,
                        long totalValidations, long successfulValidations, long failedValidations,
                        double averageValidationTimeMs) {
        this.totalRegisteredMetrics = totalRegisteredMetrics;
        this.metricsByType = metricsByType;
        this.totalValidations = totalValidations;
        this.successfulValidations = successfulValidations;
        this.failedValidations = failedValidations;
        this.averageValidationTimeMs = averageValidationTimeMs;
    }

    // Getters
    public int getTotalRegisteredMetrics() { return totalRegisteredMetrics; }
    public Map<MetricType, Integer> getMetricsByType() { return metricsByType; }
    public long getTotalValidations() { return totalValidations; }
    public long getSuccessfulValidations() { return successfulValidations; }
    public long getFailedValidations() { return failedValidations; }
    public double getAverageValidationTimeMs() { return averageValidationTimeMs; }
    public double getValidationSuccessRate() {
        return totalValidations > 0 ? (double) successfulValidations / totalValidations * 100 : 0;
    }
}

/**
 * 指标上报器接口
 */
interface MetricsReporter {

    /**
     * 初始化上报器
     * @param config 配置信息
     * @return 初始化是否成功
     */
    CompletableFuture<Boolean> initialize(Map<String, Object> config);

    /**
     * 上报单个指标
     * @param metric 指标数据
     * @return 上报结果
     */
    CompletableFuture<ReportResult> report(MetricsData metric);

    /**
     * 批量上报指标
     * @param batch 指标批次
     * @return 上报结果
     */
    CompletableFuture<ReportResult> reportBatch(MetricsBatch batch);

    /**
     * 获取上报器类型
     */
    String getReporterType();

    /**
     * 获取上报器健康状态
     */
    ReporterHealth getHealth();

    /**
     * 获取上报器统计信息
     */
    ReporterStats getStats();

    /**
     * 关闭上报器
     */
    CompletableFuture<Void> shutdown();
}

/**
 * 上报结果
 */
class ReportResult {
    private final boolean success;
    private final String message;
    private final int successCount;
    private final int failureCount;
    private final long latencyMs;
    private final Map<String, Object> details;

    public ReportResult(boolean success, String message, int successCount, int failureCount,
                       long latencyMs, Map<String, Object> details) {
        this.success = success;
        this.message = message;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.latencyMs = latencyMs;
        this.details = details != null ? details : new java.util.HashMap<>();
    }

    public static ReportResult success(int count, long latencyMs) {
        return new ReportResult(true, "Success", count, 0, latencyMs, null);
    }

    public static ReportResult failure(String message, int failureCount) {
        return new ReportResult(false, message, 0, failureCount, 0, null);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getSuccessCount() { return successCount; }
    public int getFailureCount() { return failureCount; }
    public long getLatencyMs() { return latencyMs; }
    public Map<String, Object> getDetails() { return new java.util.HashMap<>(details); }
}

/**
 * 上报器健康状态
 */
class ReporterHealth {
    private final boolean healthy;
    private final String message;
    private final long lastSuccessTime;
    private final long lastFailureTime;
    private final int consecutiveFailures;

    public ReporterHealth(boolean healthy, String message, long lastSuccessTime,
                         long lastFailureTime, int consecutiveFailures) {
        this.healthy = healthy;
        this.message = message;
        this.lastSuccessTime = lastSuccessTime;
        this.lastFailureTime = lastFailureTime;
        this.consecutiveFailures = consecutiveFailures;
    }

    // Getters
    public boolean isHealthy() { return healthy; }
    public String getMessage() { return message; }
    public long getLastSuccessTime() { return lastSuccessTime; }
    public long getLastFailureTime() { return lastFailureTime; }
    public int getConsecutiveFailures() { return consecutiveFailures; }
}

/**
 * 上报器统计信息
 */
class ReporterStats {
    private final long totalReports;
    private final long successfulReports;
    private final long failedReports;
    private final double averageLatencyMs;
    private final double throughputPerSecond;
    private final long startTime;

    public ReporterStats(long totalReports, long successfulReports, long failedReports,
                        double averageLatencyMs, double throughputPerSecond, long startTime) {
        this.totalReports = totalReports;
        this.successfulReports = successfulReports;
        this.failedReports = failedReports;
        this.averageLatencyMs = averageLatencyMs;
        this.throughputPerSecond = throughputPerSecond;
        this.startTime = startTime;
    }

    // Getters
    public long getTotalReports() { return totalReports; }
    public long getSuccessfulReports() { return successfulReports; }
    public long getFailedReports() { return failedReports; }
    public double getAverageLatencyMs() { return averageLatencyMs; }
    public double getThroughputPerSecond() { return throughputPerSecond; }
    public long getStartTime() { return startTime; }
    public double getSuccessRate() {
        return totalReports > 0 ? (double) successfulReports / totalReports * 100 : 0;
    }
    public long getUptimeMs() { return System.currentTimeMillis() - startTime; }
}
