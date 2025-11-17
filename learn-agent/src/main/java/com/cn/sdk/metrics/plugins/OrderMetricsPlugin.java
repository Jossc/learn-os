package com.cn.sdk.metrics.plugins;

import com.cn.sdk.metrics.core.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;

/**
 * 订单指标插件
 * 专门处理订单相关的业务指标收集和分析
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class OrderMetricsPlugin extends AbstractMetricsPlugin {

    // 订单统计
    private final LongAdder totalOrders = new LongAdder();
    private final LongAdder successfulOrders = new LongAdder();
    private final LongAdder failedOrders = new LongAdder();
    private final LongAdder totalRevenue = new LongAdder();

    // 订单状态跟踪
    private final Map<String, OrderInfo> activeOrders = new java.util.concurrent.ConcurrentHashMap<>();

    public OrderMetricsPlugin() {
        super("order-metrics", "1.0.0");
    }

    @Override
    public String getDescription() {
        return "订单业务指标收集插件，支持订单创建、支付、取消等全生命周期监控";
    }

    @Override
    public Set<String> getSupportedMetricTypes() {
        return Set.of("order_created", "order_paid", "order_cancelled", "order_completed", "order_failed");
    }

    @Override
    protected boolean doInitialize(Map<String, Object> config, SDKContext context) {
        try {
            // 注册订单相关指标定义
            registerOrderMetrics(context.getRegistry());

            // 初始化订单状态清理任务
            initializeOrderCleanupTask();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void registerOrderMetrics(MetricsRegistry registry) {
        // 订单总数指标
        registry.registerMetric(
            MetricDefinition.builder()
                .name("business.order.total")
                .type(MetricType.COUNTER)
                .description("订单总数")
                .unit("orders")
                .defaultTag("service", context.getConfig().getServiceName())
                .build()
        );

        // 订单成功率指标
        registry.registerMetric(
            MetricDefinition.builder()
                .name("business.order.success_rate")
                .type(MetricType.GAUGE)
                .description("订单成功率")
                .unit("percent")
                .validationRules(new ValidationRules().minValue(0).maxValue(100))
                .build()
        );

        // 订单金额分布指标
        registry.registerMetric(
            MetricDefinition.builder()
                .name("business.order.amount")
                .type(MetricType.HISTOGRAM)
                .description("订单金额分布")
                .unit("yuan")
                .validationRules(new ValidationRules().minValue(0))
                .build()
        );

        // 订单处理时长指标
        registry.registerMetric(
            MetricDefinition.builder()
                .name("business.order.processing_time")
                .type(MetricType.TIMER)
                .description("订单处理时长")
                .unit("ms")
                .build()
        );
    }

    @Override
    protected MetricsBatch doProcessMetrics(MetricsData metricData) {
        MetricsBatch batch = new MetricsBatch();

        if (metricData instanceof BusinessEventMetric) {
            BusinessEventMetric event = (BusinessEventMetric) metricData;
            processOrderEvent(event, batch);
        }

        return batch;
    }

    private void processOrderEvent(BusinessEventMetric event, MetricsBatch batch) {
        String eventType = event.getName();
        Map<String, Object> eventData = event.getEventData();
        Map<String, String> tags = event.getTags();

        switch (eventType) {
            case "order_created":
                handleOrderCreated(eventData, tags, batch);
                break;
            case "order_paid":
                handleOrderPaid(eventData, tags, batch);
                break;
            case "order_cancelled":
                handleOrderCancelled(eventData, tags, batch);
                break;
            case "order_completed":
                handleOrderCompleted(eventData, tags, batch);
                break;
            case "order_failed":
                handleOrderFailed(eventData, tags, batch);
                break;
        }
    }

    private void handleOrderCreated(Map<String, Object> eventData, Map<String, String> tags, MetricsBatch batch) {
        String orderId = (String) eventData.get("order_id");
        Long amount = (Long) eventData.get("amount");
        String userId = (String) eventData.get("user_id");

        totalOrders.increment();

        // 记录订单信息
        OrderInfo orderInfo = new OrderInfo(orderId, amount != null ? amount : 0, userId, System.currentTimeMillis());
        activeOrders.put(orderId, orderInfo);

        // 生成指标
        batch.addCounter("business.order.total", 1,
            mergeTags(tags, Map.of("status", "created", "user_id", userId != null ? userId : "unknown")));

        if (amount != null) {
            batch.addHistogram("business.order.amount", amount, tags);
        }

        batch.addBusinessEvent("order_lifecycle",
            Map.of("order_id", orderId, "stage", "created", "amount", amount, "user_id", userId), tags);
    }

    private void handleOrderPaid(Map<String, Object> eventData, Map<String, String> tags, MetricsBatch batch) {
        String orderId = (String) eventData.get("order_id");
        Long amount = (Long) eventData.get("amount");
        String paymentMethod = (String) eventData.get("payment_method");

        OrderInfo orderInfo = activeOrders.get(orderId);
        if (orderInfo != null) {
            orderInfo.setPaid(true);
            orderInfo.setPaymentTime(System.currentTimeMillis());
            orderInfo.setPaymentMethod(paymentMethod);

            // 记录订单处理时长
            long processingTime = orderInfo.getPaymentTime() - orderInfo.getCreatedTime();
            batch.addTimer("business.order.processing_time", processingTime,
                mergeTags(tags, Map.of("stage", "payment")));
        }

        if (amount != null) {
            totalRevenue.add(amount);
        }

        batch.addCounter("business.order.paid", 1,
            mergeTags(tags, Map.of("payment_method", paymentMethod != null ? paymentMethod : "unknown")));

        batch.addGauge("business.order.total_revenue", totalRevenue.sum(), tags);
    }

    private void handleOrderCompleted(Map<String, Object> eventData, Map<String, String> tags, MetricsBatch batch) {
        String orderId = (String) eventData.get("order_id");

        OrderInfo orderInfo = activeOrders.remove(orderId);
        if (orderInfo != null) {
            successfulOrders.increment();

            long totalTime = System.currentTimeMillis() - orderInfo.getCreatedTime();
            batch.addTimer("business.order.processing_time", totalTime,
                mergeTags(tags, Map.of("stage", "completed")));
        }

        batch.addCounter("business.order.completed", 1, tags);
        updateSuccessRate(batch, tags);
    }

    private void handleOrderCancelled(Map<String, Object> eventData, Map<String, String> tags, MetricsBatch batch) {
        String orderId = (String) eventData.get("order_id");
        String reason = (String) eventData.get("reason");

        activeOrders.remove(orderId);
        failedOrders.increment();

        batch.addCounter("business.order.cancelled", 1,
            mergeTags(tags, Map.of("reason", reason != null ? reason : "unknown")));

        updateSuccessRate(batch, tags);
    }

    private void handleOrderFailed(Map<String, Object> eventData, Map<String, String> tags, MetricsBatch batch) {
        String orderId = (String) eventData.get("order_id");
        String errorCode = (String) eventData.get("error_code");

        activeOrders.remove(orderId);
        failedOrders.increment();

        batch.addCounter("business.order.failed", 1,
            mergeTags(tags, Map.of("error_code", errorCode != null ? errorCode : "unknown")));

        updateSuccessRate(batch, tags);
    }

    private void updateSuccessRate(MetricsBatch batch, Map<String, String> tags) {
        long total = totalOrders.sum();
        if (total > 0) {
            double successRate = (double) successfulOrders.sum() / total * 100;
            batch.addGauge("business.order.success_rate", successRate, tags);
        }
    }

    @Override
    protected MetricsBatch doProcessBatch(MetricsBatch batch) {
        // 对整个批次进行处理，比如计算批次级别的统计信息
        MetricsBatch enhancedBatch = new MetricsBatch();

        // 复制原有指标
        batch.getMetrics().forEach(metric -> {
            if (metric.getName().startsWith("business.order")) {
                enhancedBatch.getMetrics().add(metric);
            }
        });

        // 添加批次统计
        enhancedBatch.addGauge("business.order.active_orders", activeOrders.size(),
            Map.of("service", context.getConfig().getServiceName()));

        return enhancedBatch;
    }

    @Override
    protected MetricsBatch doGenerateMetrics(Map<String, Object> context) {
        MetricsBatch batch = new MetricsBatch();

        // 生成周期性统计指标
        batch.addGauge("business.order.total_count", totalOrders.sum(),
            Map.of("service", this.context.getConfig().getServiceName()));

        batch.addGauge("business.order.successful_count", successfulOrders.sum(),
            Map.of("service", this.context.getConfig().getServiceName()));

        batch.addGauge("business.order.failed_count", failedOrders.sum(),
            Map.of("service", this.context.getConfig().getServiceName()));

        batch.addGauge("business.order.active_count", activeOrders.size(),
            Map.of("service", this.context.getConfig().getServiceName()));

        batch.addGauge("business.order.total_revenue", totalRevenue.sum(),
            Map.of("service", this.context.getConfig().getServiceName()));

        // 计算平均订单价值
        long totalOrderCount = totalOrders.sum();
        if (totalOrderCount > 0) {
            double avgOrderValue = (double) totalRevenue.sum() / totalOrderCount;
            batch.addGauge("business.order.avg_value", avgOrderValue,
                Map.of("service", this.context.getConfig().getServiceName()));
        }

        return batch;
    }

    @Override
    protected PluginHealth doHealthCheck() {
        Map<String, Object> details = new HashMap<>();
        details.put("total_orders", totalOrders.sum());
        details.put("active_orders", activeOrders.size());
        details.put("success_rate", calculateSuccessRate());

        // 检查是否有异常情况
        if (activeOrders.size() > 10000) {
            return PluginHealth.degraded("活跃订单数过多: " + activeOrders.size());
        }

        return PluginHealth.healthy();
    }

    @Override
    protected void doShutdown() {
        activeOrders.clear();
    }

    private void initializeOrderCleanupTask() {
        // 定期清理过期的订单信息
        java.util.concurrent.Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(this::cleanupExpiredOrders, 300, 300, java.util.concurrent.TimeUnit.SECONDS);
    }

    private void cleanupExpiredOrders() {
        long expireTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000; // 24小时
        activeOrders.entrySet().removeIf(entry -> entry.getValue().getCreatedTime() < expireTime);
    }

    private double calculateSuccessRate() {
        long total = totalOrders.sum();
        return total > 0 ? (double) successfulOrders.sum() / total * 100 : 0.0;
    }

    private Map<String, String> mergeTags(Map<String, String> baseTags, Map<String, String> additionalTags) {
        Map<String, String> merged = new HashMap<>(baseTags);
        merged.putAll(additionalTags);
        return merged;
    }

    /**
     * 订单信息类
     */
    private static class OrderInfo {
        private final String orderId;
        private final long amount;
        private final String userId;
        private final long createdTime;
        private volatile boolean paid = false;
        private volatile long paymentTime;
        private volatile String paymentMethod;

        public OrderInfo(String orderId, long amount, String userId, long createdTime) {
            this.orderId = orderId;
            this.amount = amount;
            this.userId = userId;
            this.createdTime = createdTime;
        }

        // Getters and Setters
        public String getOrderId() { return orderId; }
        public long getAmount() { return amount; }
        public String getUserId() { return userId; }
        public long getCreatedTime() { return createdTime; }
        public boolean isPaid() { return paid; }
        public void setPaid(boolean paid) { this.paid = paid; }
        public long getPaymentTime() { return paymentTime; }
        public void setPaymentTime(long paymentTime) { this.paymentTime = paymentTime; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
}
