package com.cn.agent.business;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 业务指标收集器
 * 负责收集订单量、活跃用户、注册用户等业务指标
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class BusinessMetricsCollector {

    private static final BusinessMetricsCollector INSTANCE = new BusinessMetricsCollector();

    // 订单相关指标
    private final LongAdder totalOrderCount = new LongAdder();
    private final LongAdder successOrderCount = new LongAdder();
    private final LongAdder failedOrderCount = new LongAdder();
    private final AtomicLong totalOrderAmount = new AtomicLong(0);

    // 用户相关指标
    private final Map<String, Long> activeUsers = new ConcurrentHashMap<>();
    private final LongAdder newUserCount = new LongAdder();
    private final LongAdder loginCount = new LongAdder();

    // 业务操作指标
    private final Map<String, LongAdder> businessOperations = new ConcurrentHashMap<>();

    // 错误统计
    private final Map<String, LongAdder> businessErrors = new ConcurrentHashMap<>();

    private BusinessMetricsCollector() {}

    public static BusinessMetricsCollector getInstance() {
        return INSTANCE;
    }

    /**
     * 记录订单创建
     */
    public void recordOrderCreated(String orderId, long amount) {
        totalOrderCount.increment();
        totalOrderAmount.addAndGet(amount);
        recordBusinessOperation("order_create");

        System.out.println(String.format("[%s] 订单创建 - ID: %s, 金额: %d, 总订单数: %d",
            getCurrentTime(), orderId, amount, totalOrderCount.sum()));
    }

    /**
     * 记录订单成功
     */
    public void recordOrderSuccess(String orderId) {
        successOrderCount.increment();
        recordBusinessOperation("order_success");

        System.out.println(String.format("[%s] 订单成功 - ID: %s, 成功订单数: %d",
            getCurrentTime(), orderId, successOrderCount.sum()));
    }

    /**
     * 记录订单失败
     */
    public void recordOrderFailed(String orderId, String reason) {
        failedOrderCount.increment();
        recordBusinessOperation("order_failed");
        recordBusinessError("order_failed", reason);

        System.out.println(String.format("[%s] 订单失败 - ID: %s, 原因: %s, 失败订单数: %d",
            getCurrentTime(), orderId, reason, failedOrderCount.sum()));
    }

    /**
     * 记录用户活跃
     */
    public void recordUserActive(String userId) {
        activeUsers.put(userId, System.currentTimeMillis());
        recordBusinessOperation("user_active");

        System.out.println(String.format("[%s] 用户活跃 - ID: %s, 活跃用户数: %d",
            getCurrentTime(), userId, getActiveUserCount()));
    }

    /**
     * 记录新用户注册
     */
    public void recordNewUser(String userId) {
        newUserCount.increment();
        recordBusinessOperation("user_register");

        System.out.println(String.format("[%s] 新用户注册 - ID: %s, 注册用户数: %d",
            getCurrentTime(), userId, newUserCount.sum()));
    }

    /**
     * 记录用户登录
     */
    public void recordUserLogin(String userId) {
        loginCount.increment();
        recordUserActive(userId);
        recordBusinessOperation("user_login");

        System.out.println(String.format("[%s] 用户登录 - ID: %s, 登录次数: %d",
            getCurrentTime(), userId, loginCount.sum()));
    }

    /**
     * 记录业务操作
     */
    public void recordBusinessOperation(String operation) {
        businessOperations.computeIfAbsent(operation, k -> new LongAdder()).increment();
    }

    /**
     * 记录业务错误
     */
    public void recordBusinessError(String errorType, String errorMessage) {
        businessErrors.computeIfAbsent(errorType, k -> new LongAdder()).increment();

        System.err.println(String.format("[%s] 业务错误 - 类型: %s, 消息: %s, 错误次数: %d",
            getCurrentTime(), errorType, errorMessage,
            businessErrors.get(errorType).sum()));
    }

    /**
     * 获取活跃用户数（最近5分钟）
     */
    public int getActiveUserCount() {
        long fiveMinutesAgo = System.currentTimeMillis() - 5 * 60 * 1000;
        return (int) activeUsers.values().stream()
            .filter(timestamp -> timestamp > fiveMinutesAgo)
            .count();
    }

    /**
     * 获取订单成功率
     */
    public double getOrderSuccessRate() {
        long total = totalOrderCount.sum();
        if (total == 0) return 0.0;
        return (double) successOrderCount.sum() / total * 100;
    }

    /**
     * 获取所有业务指标
     */
    public BusinessMetrics getBusinessMetrics() {
        BusinessMetrics metrics = new BusinessMetrics();
        metrics.totalOrderCount = totalOrderCount.sum();
        metrics.successOrderCount = successOrderCount.sum();
        metrics.failedOrderCount = failedOrderCount.sum();
        metrics.totalOrderAmount = totalOrderAmount.get();
        metrics.activeUserCount = getActiveUserCount();
        metrics.newUserCount = newUserCount.sum();
        metrics.loginCount = loginCount.sum();
        metrics.orderSuccessRate = getOrderSuccessRate();

        // 复制业务操作统计
        metrics.businessOperations = new ConcurrentHashMap<>();
        businessOperations.forEach((k, v) -> metrics.businessOperations.put(k, v.sum()));

        // 复制业务错误统计
        metrics.businessErrors = new ConcurrentHashMap<>();
        businessErrors.forEach((k, v) -> metrics.businessErrors.put(k, v.sum()));

        return metrics;
    }

    /**
     * 重置指标（用于定期清理）
     */
    public void resetMetrics() {
        // 清理过期的活跃用户
        long tenMinutesAgo = System.currentTimeMillis() - 10 * 60 * 1000;
        activeUsers.entrySet().removeIf(entry -> entry.getValue() < tenMinutesAgo);

        System.out.println(String.format("[%s] 指标重置完成", getCurrentTime()));
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 业务指标数据结构
     */
    public static class BusinessMetrics {
        public long totalOrderCount;
        public long successOrderCount;
        public long failedOrderCount;
        public long totalOrderAmount;
        public int activeUserCount;
        public long newUserCount;
        public long loginCount;
        public double orderSuccessRate;
        public Map<String, Long> businessOperations;
        public Map<String, Long> businessErrors;

        @Override
        public String toString() {
            return String.format(
                "BusinessMetrics{totalOrders=%d, successOrders=%d, failedOrders=%d, " +
                "totalAmount=%d, activeUsers=%d, newUsers=%d, logins=%d, successRate=%.2f%%}",
                totalOrderCount, successOrderCount, failedOrderCount, totalOrderAmount,
                activeUserCount, newUserCount, loginCount, orderSuccessRate
            );
        }
    }
}
