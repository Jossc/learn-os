package com.cn.sdk.metrics.impl;

import com.cn.sdk.metrics.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * 默认指标注册中心实现
 * 提供线程安全的指标定义管理和验证功能
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class DefaultMetricsRegistry implements MetricsRegistry {

    private final Map<String, MetricDefinition> metricDefinitions = new ConcurrentHashMap<>();
    private final Map<MetricType, Set<String>> metricsByType = new ConcurrentHashMap<>();

    // 统计信息
    private final LongAdder totalValidations = new LongAdder();
    private final LongAdder successfulValidations = new LongAdder();
    private final LongAdder failedValidations = new LongAdder();
    private final LongAdder totalValidationTime = new LongAdder();

    public DefaultMetricsRegistry() {
        // 初始化内置指标定义
        registerBuiltinMetrics();
    }

    private void registerBuiltinMetrics() {
        // 注册通用业务指标
        registerMetric(MetricDefinition.builder()
            .name("business.order.total")
            .type(MetricType.COUNTER)
            .description("订单总数")
            .unit("orders")
            .validationRules(new ValidationRules().minValue(0))
            .build());

        registerMetric(MetricDefinition.builder()
            .name("business.order.success_rate")
            .type(MetricType.GAUGE)
            .description("订单成功率")
            .unit("percent")
            .validationRules(new ValidationRules().minValue(0).maxValue(100))
            .build());

        registerMetric(MetricDefinition.builder()
            .name("business.user.active")
            .type(MetricType.GAUGE)
            .description("活跃用户数")
            .unit("users")
            .validationRules(new ValidationRules().minValue(0))
            .build());

        registerMetric(MetricDefinition.builder()
            .name("business.payment.amount")
            .type(MetricType.HISTOGRAM)
            .description("支付金额分布")
            .unit("yuan")
            .validationRules(new ValidationRules().minValue(0))
            .build());

        registerMetric(MetricDefinition.builder()
            .name("business.response.time")
            .type(MetricType.TIMER)
            .description("业务响应时间")
            .unit("ms")
            .validationRules(new ValidationRules().minValue(0))
            .build());
    }

    @Override
    public boolean registerMetric(MetricDefinition definition) {
        if (definition == null || definition.getName() == null) {
            return false;
        }

        try {
            metricDefinitions.put(definition.getName(), definition);

            // 按类型分组
            metricsByType.computeIfAbsent(definition.getType(), k -> ConcurrentHashMap.newKeySet())
                .add(definition.getName());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int registerMetrics(List<MetricDefinition> definitions) {
        if (definitions == null) {
            return 0;
        }

        int successCount = 0;
        for (MetricDefinition definition : definitions) {
            if (registerMetric(definition)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public boolean unregisterMetric(String metricName) {
        if (metricName == null) {
            return false;
        }

        MetricDefinition removed = metricDefinitions.remove(metricName);
        if (removed != null) {
            // 从类型分组中移除
            Set<String> typeMetrics = metricsByType.get(removed.getType());
            if (typeMetrics != null) {
                typeMetrics.remove(metricName);
            }
            return true;
        }
        return false;
    }

    @Override
    public MetricDefinition getMetricDefinition(String metricName) {
        return metricDefinitions.get(metricName);
    }

    @Override
    public Set<String> getAllMetricNames() {
        return new HashSet<>(metricDefinitions.keySet());
    }

    @Override
    public List<MetricDefinition> getMetricsByType(MetricType metricType) {
        Set<String> names = metricsByType.get(metricType);
        if (names == null) {
            return new ArrayList<>();
        }

        return names.stream()
            .map(metricDefinitions::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public List<MetricDefinition> getMetricsByTags(Map<String, String> tagFilters) {
        if (tagFilters == null || tagFilters.isEmpty()) {
            return new ArrayList<>(metricDefinitions.values());
        }

        return metricDefinitions.values().stream()
            .filter(def -> matchesTags(def, tagFilters))
            .collect(Collectors.toList());
    }

    private boolean matchesTags(MetricDefinition definition, Map<String, String> tagFilters) {
        Map<String, String> defTags = definition.getDefaultTags();

        for (Map.Entry<String, String> filter : tagFilters.entrySet()) {
            String defValue = defTags.get(filter.getKey());
            if (!Objects.equals(defValue, filter.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ValidationResult validateMetric(String metricName, MetricsData data) {
        long startTime = System.nanoTime();
        totalValidations.increment();

        try {
            MetricDefinition definition = metricDefinitions.get(metricName);
            if (definition == null) {
                // 如果没有定义，创建一个默认定义
                definition = createDefaultDefinition(metricName, data.getType());
                registerMetric(definition);
            }

            ValidationResult result = validateAgainstDefinition(definition, data);

            if (result.isValid()) {
                successfulValidations.increment();
            } else {
                failedValidations.increment();
            }

            return result;

        } catch (Exception e) {
            failedValidations.increment();
            return ValidationResult.failure("验证过程异常: " + e.getMessage());
        } finally {
            totalValidationTime.addAndGet(System.nanoTime() - startTime);
        }
    }

    private MetricDefinition createDefaultDefinition(String metricName, MetricType type) {
        return MetricDefinition.builder()
            .name(metricName)
            .type(type)
            .description("自动生成的指标定义: " + metricName)
            .unit(getDefaultUnit(type))
            .validationRules(getDefaultValidationRules(type))
            .build();
    }

    private String getDefaultUnit(MetricType type) {
        switch (type) {
            case COUNTER: return "count";
            case GAUGE: return "value";
            case HISTOGRAM: return "value";
            case TIMER: return "ms";
            case BUSINESS_EVENT: return "events";
            default: return "unknown";
        }
    }

    private ValidationRules getDefaultValidationRules(MetricType type) {
        ValidationRules rules = new ValidationRules();

        switch (type) {
            case COUNTER:
            case GAUGE:
            case HISTOGRAM:
                rules.minValue(0);
                break;
            case TIMER:
                rules.minValue(0).maxValue(Long.MAX_VALUE);
                break;
            default:
                // 业务事件不设置数值限制
                break;
        }

        return rules;
    }

    private ValidationResult validateAgainstDefinition(MetricDefinition definition, MetricsData data) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 验证类型匹配
        if (definition.getType() != data.getType()) {
            errors.add(String.format("指标类型不匹配: 期望 %s, 实际 %s",
                definition.getType(), data.getType()));
        }

        // 验证数值范围
        ValidationRules rules = definition.getValidationRules();
        if (rules != null) {
            validateValueRules(data, rules, errors, warnings);
            validateTagRules(data, rules, errors, warnings);
        }

        // 验证标签
        validateTags(definition, data, warnings);

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    private void validateValueRules(MetricsData data, ValidationRules rules,
                                   List<String> errors, List<String> warnings) {
        Double value = extractNumericValue(data);
        if (value != null) {
            if (rules.getMinValue() != null && value < rules.getMinValue()) {
                errors.add(String.format("数值 %f 小于最小值 %f", value, rules.getMinValue()));
            }
            if (rules.getMaxValue() != null && value > rules.getMaxValue()) {
                errors.add(String.format("数值 %f 大于最大值 %f", value, rules.getMaxValue()));
            }
        } else if (!rules.isAllowNullValues()) {
            errors.add("不允许空值，但检测到空值");
        }
    }

    private void validateTagRules(MetricsData data, ValidationRules rules,
                                 List<String> errors, List<String> warnings) {
        Map<String, String> tags = data.getTags();

        // 检查必需标签
        if (rules.getRequiredTags() != null) {
            for (String requiredTag : rules.getRequiredTags()) {
                if (!tags.containsKey(requiredTag)) {
                    errors.add("缺少必需标签: " + requiredTag);
                }
            }
        }

        // 检查允许的标签
        if (rules.getAllowedTags() != null) {
            for (String tag : tags.keySet()) {
                if (!rules.getAllowedTags().contains(tag)) {
                    warnings.add("使用了未定义的标签: " + tag);
                }
            }
        }
    }

    private void validateTags(MetricDefinition definition, MetricsData data, List<String> warnings) {
        Map<String, String> defaultTags = definition.getDefaultTags();
        Map<String, String> dataTags = data.getTags();

        // 检查是否使用了默认标签
        for (String defaultTag : defaultTags.keySet()) {
            if (!dataTags.containsKey(defaultTag)) {
                warnings.add("建议使用默认标签: " + defaultTag);
            }
        }
    }

    private Double extractNumericValue(MetricsData data) {
        if (data instanceof CounterMetric) {
            return (double) ((CounterMetric) data).getValue();
        } else if (data instanceof GaugeMetric) {
            return ((GaugeMetric) data).getValue();
        } else if (data instanceof HistogramMetric) {
            return ((HistogramMetric) data).getValue();
        } else if (data instanceof TimerMetric) {
            return (double) ((TimerMetric) data).getDurationMs();
        }
        return null;
    }

    @Override
    public RegistryStats getStats() {
        long total = totalValidations.sum();
        long successful = successfulValidations.sum();
        long failed = failedValidations.sum();
        double avgTime = total > 0 ? (double) totalValidationTime.sum() / total / 1_000_000 : 0; // 转换为毫秒

        // 按类型统计指标数量
        Map<MetricType, Integer> typeStats = new EnumMap<>(MetricType.class);
        for (Map.Entry<MetricType, Set<String>> entry : metricsByType.entrySet()) {
            typeStats.put(entry.getKey(), entry.getValue().size());
        }

        return new RegistryStats(metricDefinitions.size(), typeStats, total, successful, failed, avgTime);
    }
}
