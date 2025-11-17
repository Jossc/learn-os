package com.cn.sdk.metrics.core;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SDK状态管理
 * 跟踪SDK的运行状态、性能指标和健康状况
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class SDKStatus {

    private volatile SDKState state = SDKState.UNINITIALIZED;
    private final Instant creationTime = Instant.now();
    private volatile Instant lastStateChangeTime = creationTime;
    private volatile String stateMessage = "SDK created";

    // 性能统计
    private final Map<String, Long> counters = new ConcurrentHashMap<>();
    private final Map<String, Double> gauges = new ConcurrentHashMap<>();
    private final Map<String, String> properties = new ConcurrentHashMap<>();

    // 组件状态
    private final Map<String, ComponentStatus> componentStatuses = new ConcurrentHashMap<>();

    public enum SDKState {
        UNINITIALIZED("未初始化", 0),
        INITIALIZING("初始化中", 1),
        RUNNING("运行中", 2),
        DEGRADED("降级运行", 3),
        STOPPING("停止中", 4),
        STOPPED("已停止", 5),
        ERROR("错误状态", -1);

        private final String description;
        private final int priority;

        SDKState(String description, int priority) {
            this.description = description;
            this.priority = priority;
        }

        public String getDescription() { return description; }
        public int getPriority() { return priority; }
    }

    /**
     * 组件状态
     */
    public static class ComponentStatus {
        private final String name;
        private volatile boolean healthy;
        private volatile String message;
        private volatile Instant lastUpdateTime;
        private final Map<String, Object> details;

        public ComponentStatus(String name) {
            this.name = name;
            this.healthy = false;
            this.message = "未初始化";
            this.lastUpdateTime = Instant.now();
            this.details = new ConcurrentHashMap<>();
        }

        public void setHealthy(boolean healthy, String message) {
            this.healthy = healthy;
            this.message = message;
            this.lastUpdateTime = Instant.now();
        }

        public void addDetail(String key, Object value) {
            details.put(key, value);
            this.lastUpdateTime = Instant.now();
        }

        // Getters
        public String getName() { return name; }
        public boolean isHealthy() { return healthy; }
        public String getMessage() { return message; }
        public Instant getLastUpdateTime() { return lastUpdateTime; }
        public Map<String, Object> getDetails() { return new ConcurrentHashMap<>(details); }
    }

    /**
     * 更新SDK状态
     */
    public synchronized void updateState(SDKState newState, String message) {
        if (newState.getPriority() != state.getPriority()) {
            SDKState oldState = this.state;
            this.state = newState;
            this.stateMessage = message;
            this.lastStateChangeTime = Instant.now();

            // 记录状态变更
            incrementCounter("state_changes_total");
            setProperty("last_state_change", oldState.name() + " -> " + newState.name());
        }
    }

    /**
     * 更新组件状态
     */
    public void updateComponentStatus(String componentName, boolean healthy, String message) {
        ComponentStatus status = componentStatuses.computeIfAbsent(componentName, ComponentStatus::new);
        status.setHealthy(healthy, message);

        // 根据组件状态更新整体状态
        updateOverallState();
    }

    /**
     * 添加组件详细信息
     */
    public void addComponentDetail(String componentName, String key, Object value) {
        ComponentStatus status = componentStatuses.computeIfAbsent(componentName, ComponentStatus::new);
        status.addDetail(key, value);
    }

    /**
     * 增加计数器
     */
    public void incrementCounter(String name) {
        incrementCounter(name, 1L);
    }

    public void incrementCounter(String name, long delta) {
        counters.merge(name, delta, Long::sum);
    }

    /**
     * 设置计量器
     */
    public void setGauge(String name, double value) {
        gauges.put(name, value);
    }

    /**
     * 设置属性
     */
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    /**
     * 根据组件状态更新整体状态
     */
    private void updateOverallState() {
        if (state == SDKState.UNINITIALIZED || state == SDKState.INITIALIZING ||
            state == SDKState.STOPPING || state == SDKState.STOPPED) {
            return; // 这些状态不受组件影响
        }

        long unhealthyComponents = componentStatuses.values().stream()
            .mapToLong(status -> status.isHealthy() ? 0 : 1)
            .sum();

        if (unhealthyComponents == 0) {
            if (state != SDKState.RUNNING) {
                updateState(SDKState.RUNNING, "所有组件健康");
            }
        } else if (unhealthyComponents < componentStatuses.size() / 2) {
            if (state != SDKState.DEGRADED) {
                updateState(SDKState.DEGRADED, String.format("%d个组件不健康", unhealthyComponents));
            }
        } else {
            updateState(SDKState.ERROR, String.format("大部分组件不健康 (%d/%d)",
                unhealthyComponents, componentStatuses.size()));
        }
    }

    /**
     * 获取整体健康状态
     */
    public boolean isHealthy() {
        return state == SDKState.RUNNING || state == SDKState.DEGRADED;
    }

    /**
     * 获取详细状态报告
     */
    public StatusReport getDetailedReport() {
        return new StatusReport(
            state,
            stateMessage,
            creationTime,
            lastStateChangeTime,
            new ConcurrentHashMap<>(counters),
            new ConcurrentHashMap<>(gauges),
            new ConcurrentHashMap<>(properties),
            new ConcurrentHashMap<>(componentStatuses)
        );
    }

    // 基础Getters
    public SDKState getState() { return state; }
    public String getStateMessage() { return stateMessage; }
    public Instant getCreationTime() { return creationTime; }
    public Instant getLastStateChangeTime() { return lastStateChangeTime; }
    public long getUptimeMs() { return java.time.Duration.between(creationTime, Instant.now()).toMillis(); }

    /**
     * 状态报告数据类
     */
    public static class StatusReport {
        private final SDKState state;
        private final String message;
        private final Instant creationTime;
        private final Instant lastStateChangeTime;
        private final Map<String, Long> counters;
        private final Map<String, Double> gauges;
        private final Map<String, String> properties;
        private final Map<String, ComponentStatus> componentStatuses;

        public StatusReport(SDKState state, String message, Instant creationTime, Instant lastStateChangeTime,
                           Map<String, Long> counters, Map<String, Double> gauges, Map<String, String> properties,
                           Map<String, ComponentStatus> componentStatuses) {
            this.state = state;
            this.message = message;
            this.creationTime = creationTime;
            this.lastStateChangeTime = lastStateChangeTime;
            this.counters = counters;
            this.gauges = gauges;
            this.properties = properties;
            this.componentStatuses = componentStatuses;
        }

        // Getters
        public SDKState getState() { return state; }
        public String getMessage() { return message; }
        public Instant getCreationTime() { return creationTime; }
        public Instant getLastStateChangeTime() { return lastStateChangeTime; }
        public Map<String, Long> getCounters() { return counters; }
        public Map<String, Double> getGauges() { return gauges; }
        public Map<String, String> getProperties() { return properties; }
        public Map<String, ComponentStatus> getComponentStatuses() { return componentStatuses; }

        public long getUptimeMs() {
            return java.time.Duration.between(creationTime, Instant.now()).toMillis();
        }

        public boolean isHealthy() {
            return state == SDKState.RUNNING || state == SDKState.DEGRADED;
        }

        public long getHealthyComponentCount() {
            return componentStatuses.values().stream().mapToLong(s -> s.isHealthy() ? 1 : 0).sum();
        }

        public long getTotalComponentCount() {
            return componentStatuses.size();
        }

        @Override
        public String toString() {
            return String.format("StatusReport{state=%s, message='%s', uptime=%dms, components=%d/%d healthy}",
                state, message, getUptimeMs(), getHealthyComponentCount(), getTotalComponentCount());
        }

        /**
         * 转换为JSON格式
         */
        public Map<String, Object> toJson() {
            Map<String, Object> json = new java.util.HashMap<>();
            json.put("state", state.name());
            json.put("state_description", state.getDescription());
            json.put("message", message);
            json.put("creation_time", creationTime.toString());
            json.put("last_state_change_time", lastStateChangeTime.toString());
            json.put("uptime_ms", getUptimeMs());
            json.put("healthy", isHealthy());
            json.put("counters", counters);
            json.put("gauges", gauges);
            json.put("properties", properties);

            // 组件状态
            Map<String, Object> components = new java.util.HashMap<>();
            componentStatuses.forEach((name, status) -> {
                Map<String, Object> componentJson = new java.util.HashMap<>();
                componentJson.put("healthy", status.isHealthy());
                componentJson.put("message", status.getMessage());
                componentJson.put("last_update_time", status.getLastUpdateTime().toString());
                componentJson.put("details", status.getDetails());
                components.put(name, componentJson);
            });
            json.put("components", components);

            return json;
        }
    }
}
