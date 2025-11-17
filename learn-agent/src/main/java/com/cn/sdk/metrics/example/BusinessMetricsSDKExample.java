package com.cn.sdk.metrics.example;

import com.cn.sdk.metrics.core.*;
import com.cn.sdk.metrics.impl.DefaultBusinessMetricsSDK;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 业务指标SDK完整使用示例
 * 演示如何在真实业务场景中使用SDK
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class BusinessMetricsSDKExample {

    private final BusinessMetricsSDK sdk;
    private final ScheduledExecutorService simulator;

    public BusinessMetricsSDKExample() {
        // 1. 构建SDK配置
        SDKConfig config = SDKConfig.builder()
            .serviceName("ecommerce-service")
            .version("2.0.0")
            .environment("production")
            .reportInterval(30) // 30秒上报一次
            .maxBatchSize(500)
            .bufferSize(5000)
            .skyWalkingOapServer("127.0.0.1:11800")
            .enableSkyWalking(true)
            .enableConsole(true) // 同时启用控制台输出便于调试
            .enabledPlugins("order", "user", "payment", "system")
            .maxMemoryUsage(200) // 最大200MB内存使用
            .enableSelfMonitoring(true)
            .customProperty("business_domain", "ecommerce")
            .customProperty("datacenter", "beijing")
            .build();

        // 2. 初始化SDK
        this.sdk = DefaultBusinessMetricsSDK.create(config);
        this.simulator = Executors.newScheduledThreadPool(3);

        System.out.println("🚀 业务指标SDK示例启动成功！");
        System.out.println("SDK状态: " + sdk.getStatus().getState());
    }

    /**
     * 启动业务场景模拟
     */
    public void startBusinessSimulation() {
        System.out.println("📊 开始模拟业务场景...");

        // 模拟用户注册和登录
        simulator.scheduleAtFixedRate(this::simulateUserActivity, 0, 2, TimeUnit.SECONDS);

        // 模拟订单创建流程
        simulator.scheduleAtFixedRate(this::simulateOrderFlow, 1, 5, TimeUnit.SECONDS);

        // 模拟支付流程
        simulator.scheduleAtFixedRate(this::simulatePaymentFlow, 2, 7, TimeUnit.SECONDS);

        // 定期输出SDK状态
        simulator.scheduleAtFixedRate(this::printSDKStatus, 30, 60, TimeUnit.SECONDS);
    }

    /**
     * 模拟用户活动
     */
    private void simulateUserActivity() {
        try {
            MetricsCollector collector = sdk.getCollector();

            if (ThreadLocalRandom.current().nextInt(10) < 3) {
                // 30%概率新用户注册
                String userId = "user_" + System.currentTimeMillis() + "_" + ThreadLocalRandom.current().nextInt(1000);

                collector.recordUserAction(userId, "register", Map.of(
                    "channel", getRandomChannel(),
                    "device_type", getRandomDevice(),
                    "region", "beijing"
                ));

                System.out.println("👤 用户注册: " + userId);
            }

            // 用户登录
            String userId = "user_" + ThreadLocalRandom.current().nextInt(1000, 5000);
            collector.recordUserAction(userId, "login", Map.of(
                "device_type", getRandomDevice(),
                "login_method", getRandomLoginMethod()
            ));

            // 用户浏览
            collector.recordUserAction(userId, "browse", Map.of(
                "page_type", getRandomPageType(),
                "duration_seconds", ThreadLocalRandom.current().nextInt(10, 300)
            ));

        } catch (Exception e) {
            System.err.println("用户活动模拟异常: " + e.getMessage());
        }
    }

    /**
     * 模拟订单流程
     */
    private void simulateOrderFlow() {
        try {
            MetricsCollector collector = sdk.getCollector();

            String orderId = "ORDER_" + System.currentTimeMillis() + "_" + ThreadLocalRandom.current().nextInt(1000);
            String userId = "user_" + ThreadLocalRandom.current().nextInt(1000, 5000);
            long amount = ThreadLocalRandom.current().nextLong(100, 50000); // 1元到500元
            String[] productIds = {"product_" + ThreadLocalRandom.current().nextInt(1, 100)};

            // 1. 订单创建
            collector.recordOrderCreated(orderId, amount, userId, productIds, Map.of(
                "order_type", getRandomOrderType(),
                "source", getRandomOrderSource(),
                "discount_amount", ThreadLocalRandom.current().nextLong(0, amount / 10)
            ));

            System.out.println("📦 订单创建: " + orderId + ", 金额: ¥" + amount);

            // 2. 模拟订单处理结果（延迟执行）
            simulator.schedule(() -> {
                try {
                    if (ThreadLocalRandom.current().nextInt(100) < 85) { // 85%成功率
                        collector.recordOrderSuccess(orderId, Map.of(
                            "processing_time_ms", ThreadLocalRandom.current().nextLong(1000, 5000),
                            "warehouse", getRandomWarehouse()
                        ));
                        System.out.println("✅ 订单成功: " + orderId);
                    } else {
                        collector.recordOrderFailed(orderId, getRandomFailureReason(), Map.of(
                            "failure_stage", getRandomFailureStage()
                        ));
                        System.out.println("❌ 订单失败: " + orderId);
                    }
                } catch (Exception e) {
                    System.err.println("订单结果处理异常: " + e.getMessage());
                }
            }, ThreadLocalRandom.current().nextInt(2, 10), TimeUnit.SECONDS);

        } catch (Exception e) {
            System.err.println("订单流程模拟异常: " + e.getMessage());
        }
    }

    /**
     * 模拟支付流程
     */
    private void simulatePaymentFlow() {
        try {
            MetricsCollector collector = sdk.getCollector();

            String orderId = "ORDER_" + ThreadLocalRandom.current().nextInt(100000, 999999);
            long amount = ThreadLocalRandom.current().nextLong(100, 10000);
            String paymentMethod = getRandomPaymentMethod();
            boolean success = ThreadLocalRandom.current().nextInt(100) < 92; // 92%支付成功率

            collector.recordPayment(orderId, amount, paymentMethod, success, Map.of(
                "payment_channel", getRandomPaymentChannel(),
                "currency", "CNY",
                "risk_score", ThreadLocalRandom.current().nextInt(0, 100)
            ));

            String status = success ? "成功" : "失败";
            System.out.println("💳 支付" + status + ": " + orderId + ", " + paymentMethod + ", ¥" + amount);

        } catch (Exception e) {
            System.err.println("支付流程模拟异常: " + e.getMessage());
        }
    }

    /**
     * 演示自定义指标收集
     */
    public void demonstrateCustomMetrics() {
        System.out.println("🔧 演示自定义指标收集...");

        try {
            MetricsCollector collector = sdk.getCollector();

            // 1. 使用计时器
            TimerHandle timer = collector.startTimer("business.custom.operation",
                Map.of("operation_type", "data_processing"));

            // 模拟业务操作
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 1000));

            timer.stop(Map.of("result", "success"));

            // 2. 记录自定义业务事件
            collector.recordCustomEvent("inventory_check", Map.of(
                "product_id", "product_123",
                "stock_level", 45,
                "warehouse_id", "warehouse_bj_01",
                "check_result", "sufficient"
            ));

            // 3. 使用指标构建器
            collector.metric("business.custom.conversion")
                .tag("funnel_stage", "checkout")
                .tag("user_segment", "premium")
                .increment(1);

            // 4. 记录复杂的业务指标
            collector.recordGauge("business.custom.queue_depth",
                ThreadLocalRandom.current().nextDouble(0, 100),
                Map.of("queue_name", "order_processing", "priority", "high"));

            System.out.println("✅ 自定义指标收集完成");

        } catch (Exception e) {
            System.err.println("自定义指标收集异常: " + e.getMessage());
        }
    }

    /**
     * 演示批量指标处理
     */
    public void demonstrateBatchProcessing() {
        System.out.println("📦 演示批量指标处理...");

        try {
            MetricsCollector collector = sdk.getCollector();

            // 创建批量指标
            MetricsBatch batch = new MetricsBatch()
                .withCommonTag("batch_id", "batch_" + System.currentTimeMillis())
                .withCommonTag("service", "ecommerce-service")
                .addCounter("business.batch.processed_items", 150, Map.of("item_type", "order"))
                .addCounter("business.batch.processed_items", 89, Map.of("item_type", "payment"))
                .addGauge("business.batch.processing_speed", 23.5, Map.of("unit", "items_per_second"))
                .addHistogram("business.batch.item_size", 1024.0, Map.of("unit", "bytes"))
                .addTimer("business.batch.total_time", 5500, Map.of("stage", "complete"))
                .addBusinessEvent("batch_completed", Map.of(
                    "total_items", 239,
                    "success_rate", 98.7,
                    "error_count", 3
                ), Map.of("batch_type", "scheduled"));

            // 批量提交
            collector.recordBatch(batch);

            System.out.println("✅ 批量指标处理完成，批次大小: " + batch.size());

        } catch (Exception e) {
            System.err.println("批量指标处理异常: " + e.getMessage());
        }
    }

    /**
     * 打印SDK状态信息
     */
    private void printSDKStatus() {
        try {
            SDKStatus.StatusReport report = sdk.getStatus().getDetailedReport();

            System.out.println("\n" + "=".repeat(80));
            System.out.println("📊 SDK状态报告 - " + java.time.Instant.now());
            System.out.println("=".repeat(80));
            System.out.println("状态: " + report.getState() + " - " + report.getMessage());
            System.out.println("运行时间: " + (report.getUptimeMs() / 1000) + "秒");
            System.out.println("健康组件: " + report.getHealthyComponentCount() + "/" + report.getTotalComponentCount());

            System.out.println("\n📈 性能指标:");
            report.getCounters().forEach((name, value) ->
                System.out.println("  " + name + ": " + value));

            report.getGauges().forEach((name, value) ->
                System.out.println("  " + name + ": " + String.format("%.2f", value)));

            System.out.println("\n🔧 组件状态:");
            report.getComponentStatuses().forEach((name, status) -> {
                String health = status.isHealthy() ? "✅" : "❌";
                System.out.println("  " + health + " " + name + ": " + status.getMessage());
            });

            // 收集器统计
            CollectorStats collectorStats = sdk.getCollector().getStats();
            System.out.println("\n📊 收集器统计:");
            System.out.println("  收集指标总数: " + collectorStats.getTotalMetricsCollected());
            System.out.println("  丢弃指标数: " + collectorStats.getTotalMetricsDropped());
            System.out.println("  平均延迟: " + String.format("%.2fms", collectorStats.getAverageLatencyMs()));
            System.out.println("  缓冲区使用率: " + String.format("%.1f%%", collectorStats.getBufferUsagePercent()));

            System.out.println("=".repeat(80));

        } catch (Exception e) {
            System.err.println("状态报告生成异常: " + e.getMessage());
        }
    }

    /**
     * 关闭示例程序
     */
    public void shutdown() {
        System.out.println("🛑 关闭SDK示例程序...");

        try {
            // 停止模拟器
            simulator.shutdown();
            if (!simulator.awaitTermination(5, TimeUnit.SECONDS)) {
                simulator.shutdownNow();
            }

            // 最后一次状态报告
            printSDKStatus();

            // 关闭SDK
            sdk.shutdown();

            System.out.println("✅ SDK示例程序已关闭");

        } catch (Exception e) {
            System.err.println("关闭异常: " + e.getMessage());
        }
    }

    // 辅助方法 - 生成随机测试数据
    private String getRandomChannel() {
        String[] channels = {"web", "mobile_app", "wechat", "alipay", "api"};
        return channels[ThreadLocalRandom.current().nextInt(channels.length)];
    }

    private String getRandomDevice() {
        String[] devices = {"android", "ios", "web", "desktop"};
        return devices[ThreadLocalRandom.current().nextInt(devices.length)];
    }

    private String getRandomLoginMethod() {
        String[] methods = {"password", "sms", "wechat", "fingerprint", "face_id"};
        return methods[ThreadLocalRandom.current().nextInt(methods.length)];
    }

    private String getRandomPageType() {
        String[] pages = {"homepage", "product_list", "product_detail", "cart", "checkout", "profile"};
        return pages[ThreadLocalRandom.current().nextInt(pages.length)];
    }

    private String getRandomOrderType() {
        String[] types = {"normal", "presale", "group_buy", "flash_sale"};
        return types[ThreadLocalRandom.current().nextInt(types.length)];
    }

    private String getRandomOrderSource() {
        String[] sources = {"web", "mobile", "wechat_mini", "api", "third_party"};
        return sources[ThreadLocalRandom.current().nextInt(sources.length)];
    }

    private String getRandomWarehouse() {
        String[] warehouses = {"warehouse_bj", "warehouse_sh", "warehouse_gz", "warehouse_sz"};
        return warehouses[ThreadLocalRandom.current().nextInt(warehouses.length)];
    }

    private String getRandomFailureReason() {
        String[] reasons = {"inventory_insufficient", "payment_failed", "user_cancelled", "system_error", "fraud_detected"};
        return reasons[ThreadLocalRandom.current().nextInt(reasons.length)];
    }

    private String getRandomFailureStage() {
        String[] stages = {"validation", "inventory_check", "payment", "fulfillment", "shipping"};
        return stages[ThreadLocalRandom.current().nextInt(stages.length)];
    }

    private String getRandomPaymentMethod() {
        String[] methods = {"alipay", "wechat_pay", "union_pay", "credit_card", "bank_transfer"};
        return methods[ThreadLocalRandom.current().nextInt(methods.length)];
    }

    private String getRandomPaymentChannel() {
        String[] channels = {"online", "offline", "mobile", "api"};
        return channels[ThreadLocalRandom.current().nextInt(channels.length)];
    }

    /**
     * 主程序入口
     */
    public static void main(String[] args) {
        BusinessMetricsSDKExample example = new BusinessMetricsSDKExample();

        // 启动业务模拟
        example.startBusinessSimulation();

        // 演示自定义指标
        example.demonstrateCustomMetrics();

        // 演示批量处理
        example.demonstrateBatchProcessing();

        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(example::shutdown));

        System.out.println("🎯 业务指标SDK示例正在运行...");
        System.out.println("按Ctrl+C停止程序");

        // 保持程序运行
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
