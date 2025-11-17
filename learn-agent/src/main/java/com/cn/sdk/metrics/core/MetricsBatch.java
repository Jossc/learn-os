package com.cn.sdk.metrics.core;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 指标批处理类
 * 用于批量收集和处理指标数据
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class MetricsBatch {

    private final List<MetricsData> metrics;
    private final Map<String, String> commonTags;
    private final Instant timestamp;
    private final String batchId;

    public MetricsBatch() {
        this.metrics = new ArrayList<>();
        this.commonTags = new ConcurrentHashMap<>();
        this.timestamp = Instant.now();
        this.batchId = UUID.randomUUID().toString();
    }

    /**
     * 添加计数器指标
     */
    public MetricsBatch addCounter(String name, long value, Map<String, String> tags) {
        metrics.add(new CounterMetric(name, value, mergeTags(tags), timestamp));
        return this;
    }

    /**
     * 添加计量器指标
     */
    public MetricsBatch addGauge(String name, double value, Map<String, String> tags) {
        metrics.add(new GaugeMetric(name, value, mergeTags(tags), timestamp));
        return this;
    }

    /**
     * 添加直方图指标
     */
    public MetricsBatch addHistogram(String name, double value, Map<String, String> tags) {
        metrics.add(new HistogramMetric(name, value, mergeTags(tags), timestamp));
        return this;
    }

    /**
     * 添加计时器指标
     */
    public MetricsBatch addTimer(String name, long durationMs, Map<String, String> tags) {
        metrics.add(new TimerMetric(name, durationMs, mergeTags(tags), timestamp));
        return this;
    }

    /**
     * 添加业务事件
     */
    public MetricsBatch addBusinessEvent(String eventType, Map<String, Object> eventData, Map<String, String> tags) {
        metrics.add(new BusinessEventMetric(eventType, eventData, mergeTags(tags), timestamp));
        return this;
    }

    /**
     * 设置公共标签
     */
    public MetricsBatch withCommonTag(String key, String value) {
        commonTags.put(key, value);
        return this;
    }

    /**
     * 设置公共标签
     */
    public MetricsBatch withCommonTags(Map<String, String> tags) {
        if (tags != null) {
            commonTags.putAll(tags);
        }
        return this;
    }

    /**
     * 合并标签（公共标签 + 特定标签）
     */
    private Map<String, String> mergeTags(Map<String, String> specificTags) {
        Map<String, String> merged = new HashMap<>(commonTags);
        if (specificTags != null) {
            merged.putAll(specificTags);
        }
        return merged;
    }

    // Getters
    public List<MetricsData> getMetrics() { return new ArrayList<>(metrics); }
    public Map<String, String> getCommonTags() { return new HashMap<>(commonTags); }
    public Instant getTimestamp() { return timestamp; }
    public String getBatchId() { return batchId; }
    public int size() { return metrics.size(); }
    public boolean isEmpty() { return metrics.isEmpty(); }

    @Override
    public String toString() {
        return String.format("MetricsBatch{id=%s, size=%d, timestamp=%s}",
            batchId.substring(0, 8), metrics.size(), timestamp);
    }
}

/**
 * 指标数据基类
 */
abstract class MetricsData {
    protected final String name;
    protected final Map<String, String> tags;
    protected final Instant timestamp;
    protected final MetricType type;

    protected MetricsData(String name, Map<String, String> tags, Instant timestamp, MetricType type) {
        this.name = name;
        this.tags = tags != null ? new HashMap<>(tags) : new HashMap<>();
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getName() { return name; }
    public Map<String, String> getTags() { return new HashMap<>(tags); }
    public Instant getTimestamp() { return timestamp; }
    public MetricType getType() { return type; }

    /**
     * 转换为SkyWalking格式
     */
    public abstract Map<String, Object> toSkyWalkingFormat();

    /**
     * 转换为Prometheus格式
     */
    public abstract String toPrometheusFormat();

    /**
     * 转换为Elasticsearch格式
     */
    public abstract Map<String, Object> toElasticsearchFormat();
}

/**
 * 计数器指标
 */
class CounterMetric extends MetricsData {
    private final long value;

    public CounterMetric(String name, long value, Map<String, String> tags, Instant timestamp) {
        super(name, tags, timestamp, MetricType.COUNTER);
        this.value = value;
    }

    public long getValue() { return value; }

    @Override
    public Map<String, Object> toSkyWalkingFormat() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("type", "counter");
        data.put("value", value);
        data.put("tags", tags);
        data.put("timestamp", timestamp.toEpochMilli());
        return data;
    }

    @Override
    public String toPrometheusFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.replace('.', '_')).append("_total");
        if (!tags.isEmpty()) {
            sb.append("{");
            tags.forEach((k, v) -> sb.append(k).append("=\"").append(v).append("\","));
            sb.setLength(sb.length() - 1); // 删除最后的逗号
            sb.append("}");
        }
        sb.append(" ").append(value);
        sb.append(" ").append(timestamp.toEpochMilli());
        return sb.toString();
    }

    @Override
    public Map<String, Object> toElasticsearchFormat() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("@timestamp", timestamp.toString());
        doc.put("metric_name", name);
        doc.put("metric_type", "counter");
        doc.put("value", value);
        doc.put("tags", tags);
        return doc;
    }
}

/**
 * 计量器指标
 */
class GaugeMetric extends MetricsData {
    private final double value;

    public GaugeMetric(String name, double value, Map<String, String> tags, Instant timestamp) {
        super(name, tags, timestamp, MetricType.GAUGE);
        this.value = value;
    }

    public double getValue() { return value; }

    @Override
    public Map<String, Object> toSkyWalkingFormat() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("type", "gauge");
        data.put("value", value);
        data.put("tags", tags);
        data.put("timestamp", timestamp.toEpochMilli());
        return data;
    }

    @Override
    public String toPrometheusFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.replace('.', '_'));
        if (!tags.isEmpty()) {
            sb.append("{");
            tags.forEach((k, v) -> sb.append(k).append("=\"").append(v).append("\","));
            sb.setLength(sb.length() - 1);
            sb.append("}");
        }
        sb.append(" ").append(value);
        sb.append(" ").append(timestamp.toEpochMilli());
        return sb.toString();
    }

    @Override
    public Map<String, Object> toElasticsearchFormat() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("@timestamp", timestamp.toString());
        doc.put("metric_name", name);
        doc.put("metric_type", "gauge");
        doc.put("value", value);
        doc.put("tags", tags);
        return doc;
    }
}

/**
 * 直方图指标
 */
class HistogramMetric extends MetricsData {
    private final double value;

    public HistogramMetric(String name, double value, Map<String, String> tags, Instant timestamp) {
        super(name, tags, timestamp, MetricType.HISTOGRAM);
        this.value = value;
    }

    public double getValue() { return value; }

    @Override
    public Map<String, Object> toSkyWalkingFormat() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("type", "histogram");
        data.put("value", value);
        data.put("tags", tags);
        data.put("timestamp", timestamp.toEpochMilli());
        return data;
    }

    @Override
    public String toPrometheusFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.replace('.', '_')).append("_bucket");
        if (!tags.isEmpty()) {
            sb.append("{");
            tags.forEach((k, v) -> sb.append(k).append("=\"").append(v).append("\","));
            sb.setLength(sb.length() - 1);
            sb.append("}");
        }
        sb.append(" ").append(value);
        sb.append(" ").append(timestamp.toEpochMilli());
        return sb.toString();
    }

    @Override
    public Map<String, Object> toElasticsearchFormat() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("@timestamp", timestamp.toString());
        doc.put("metric_name", name);
        doc.put("metric_type", "histogram");
        doc.put("value", value);
        doc.put("tags", tags);
        return doc;
    }
}

/**
 * 计时器指标
 */
class TimerMetric extends MetricsData {
    private final long durationMs;

    public TimerMetric(String name, long durationMs, Map<String, String> tags, Instant timestamp) {
        super(name, tags, timestamp, MetricType.TIMER);
        this.durationMs = durationMs;
    }

    public long getDurationMs() { return durationMs; }

    @Override
    public Map<String, Object> toSkyWalkingFormat() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("type", "timer");
        data.put("duration_ms", durationMs);
        data.put("tags", tags);
        data.put("timestamp", timestamp.toEpochMilli());
        return data;
    }

    @Override
    public String toPrometheusFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.replace('.', '_')).append("_duration_ms");
        if (!tags.isEmpty()) {
            sb.append("{");
            tags.forEach((k, v) -> sb.append(k).append("=\"").append(v).append("\","));
            sb.setLength(sb.length() - 1);
            sb.append("}");
        }
        sb.append(" ").append(durationMs);
        sb.append(" ").append(timestamp.toEpochMilli());
        return sb.toString();
    }

    @Override
    public Map<String, Object> toElasticsearchFormat() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("@timestamp", timestamp.toString());
        doc.put("metric_name", name);
        doc.put("metric_type", "timer");
        doc.put("duration_ms", durationMs);
        doc.put("tags", tags);
        return doc;
    }
}

/**
 * 业务事件指标
 */
class BusinessEventMetric extends MetricsData {
    private final Map<String, Object> eventData;

    public BusinessEventMetric(String eventType, Map<String, Object> eventData,
                              Map<String, String> tags, Instant timestamp) {
        super(eventType, tags, timestamp, MetricType.BUSINESS_EVENT);
        this.eventData = eventData != null ? new HashMap<>(eventData) : new HashMap<>();
    }

    public Map<String, Object> getEventData() { return new HashMap<>(eventData); }

    @Override
    public Map<String, Object> toSkyWalkingFormat() {
        Map<String, Object> data = new HashMap<>();
        data.put("event_type", name);
        data.put("type", "business_event");
        data.put("event_data", eventData);
        data.put("tags", tags);
        data.put("timestamp", timestamp.toEpochMilli());
        return data;
    }

    @Override
    public String toPrometheusFormat() {
        // 业务事件不太适合Prometheus格式，转换为计数器
        StringBuilder sb = new StringBuilder();
        sb.append("business_event_").append(name.replace('.', '_')).append("_total");
        if (!tags.isEmpty()) {
            sb.append("{");
            tags.forEach((k, v) -> sb.append(k).append("=\"").append(v).append("\","));
            sb.setLength(sb.length() - 1);
            sb.append("}");
        }
        sb.append(" 1");
        sb.append(" ").append(timestamp.toEpochMilli());
        return sb.toString();
    }

    @Override
    public Map<String, Object> toElasticsearchFormat() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("@timestamp", timestamp.toString());
        doc.put("event_type", name);
        doc.put("metric_type", "business_event");
        doc.put("event_data", eventData);
        doc.put("tags", tags);
        return doc;
    }
}

/**
 * 指标类型枚举
 */
enum MetricType {
    COUNTER,
    GAUGE,
    HISTOGRAM,
    TIMER,
    BUSINESS_EVENT
}
