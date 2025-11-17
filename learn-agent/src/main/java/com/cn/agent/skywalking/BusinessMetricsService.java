package com.cn.agent.skywalking;

import com.cn.agent.business.BusinessMetricsCollector;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.DefaultImplementor;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.meter.MeterService;
import org.apache.skywalking.apm.agent.core.meter.Counter;
import org.apache.skywalking.apm.agent.core.meter.Gauge;
import org.apache.skywalking.apm.agent.core.meter.Histogram;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SkyWalking业务指标服务
 * 负责将业务指标注册到SkyWalking的MeterService并定期上报
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
@DefaultImplementor
public class BusinessMetricsService implements BootService {

    private static final ILog LOGGER = LogManager.getLogger(BusinessMetricsService.class);
    private ScheduledExecutorService scheduler;
    private MeterService meterService;

    // 业务指标计数器
    private Counter totalOrdersCounter;
    private Counter successOrdersCounter;
    private Counter failedOrdersCounter;
    private Counter newUsersCounter;
    private Counter userLoginsCounter;
    private Gauge activeUsersGauge;
    private Gauge orderSuccessRateGauge;
    private Gauge totalRevenueGauge;
    private Histogram orderAmountHistogram;

    @Override
    public void prepare() throws Throwable {
        LOGGER.info("准备业务指标服务...");
        scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void boot() throws Throwable {
        LOGGER.info("启动业务指标服务...");

        // 获取MeterService实例
        meterService = MeterService.getInstance();

        // 注册业务指标
        registerBusinessMetrics();

        // 启动定期上报任务
        startMetricsReporting();

        LOGGER.info("业务指标服务启动完成");
    }

    @Override
    public void onComplete() throws Throwable {
        LOGGER.info("业务指标服务初始化完成");
    }

    @Override
    public void shutdown() throws Throwable {
        LOGGER.info("关闭业务指标服务...");
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("业务指标服务已关闭");
    }

    /**
     * 注册业务指标到SkyWalking
     */
    private void registerBusinessMetrics() {
        try {
            // 订单相关指标
            totalOrdersCounter = meterService.getCounter("business_total_orders")
                .tag("service", Config.Agent.SERVICE_NAME)
                .build();

            successOrdersCounter = meterService.getCounter("business_successful_orders")
                .tag("service", Config.Agent.SERVICE_NAME)
                .build();

            failedOrdersCounter = meterService.getCounter("business_failed_orders")
                .tag("service", Config.Agent.SERVICE_NAME)
                .build();

            // 用户相关指标
            newUsersCounter = meterService.getCounter("business_new_users")
                .tag("service", Config.Agent.SERVICE_NAME)
                .build();

            userLoginsCounter = meterService.getCounter("business_user_logins")
                .tag("service", Config.Agent.SERVICE_NAME)
                .build();

            // 实时指标（Gauge）
            activeUsersGauge = meterService.getGauge("business_active_users")
                .tag("service", Config.Agent.SERVICE_NAME)
                .build();

            orderSuccessRateGauge = meterService.getGauge("business_order_success_rate")
                .tag("service", Config.Agent.SERVICE_NAME)
                .build();

            totalRevenueGauge = meterService.getGauge("business_total_revenue")
                .tag("service", Config.Agent.SERVICE_NAME)
                .build();

            // 订单金额分布直方图
            orderAmountHistogram = meterService.getHistogram("business_order_amount")
                .tag("service", Config.Agent.SERVICE_NAME)
                .steps(100, 500, 1000, 5000, 10000)
                .build();

            LOGGER.info("业务指标注册完成");

        } catch (Exception e) {
            LOGGER.error("注册业务指标失败", e);
        }
    }

    /**
     * 启动指标上报任务
     */
    private void startMetricsReporting() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                reportMetrics();
            } catch (Exception e) {
                LOGGER.error("上报业务指标失败", e);
            }
        }, 10, 30, TimeUnit.SECONDS); // 每30秒上报一次

        LOGGER.info("业务指标上报任务已启动，间隔30秒");
    }

    /**
     * 上报业务指标
     */
    private void reportMetrics() {
        try {
            BusinessMetricsCollector collector = BusinessMetricsCollector.getInstance();
            BusinessMetricsCollector.BusinessMetrics metrics = collector.getBusinessMetrics();

            // 更新计数器（增量）
            long currentTotalOrders = totalOrdersCounter.get();
            if (metrics.totalOrderCount > currentTotalOrders) {
                totalOrdersCounter.increment(metrics.totalOrderCount - currentTotalOrders);
            }

            long currentSuccessOrders = successOrdersCounter.get();
            if (metrics.successOrderCount > currentSuccessOrders) {
                successOrdersCounter.increment(metrics.successOrderCount - currentSuccessOrders);
            }

            long currentFailedOrders = failedOrdersCounter.get();
            if (metrics.failedOrderCount > currentFailedOrders) {
                failedOrdersCounter.increment(metrics.failedOrderCount - currentFailedOrders);
            }

            long currentNewUsers = newUsersCounter.get();
            if (metrics.newUserCount > currentNewUsers) {
                newUsersCounter.increment(metrics.newUserCount - currentNewUsers);
            }

            long currentLogins = userLoginsCounter.get();
            if (metrics.loginCount > currentLogins) {
                userLoginsCounter.increment(metrics.loginCount - currentLogins);
            }

            // 更新实时指标
            activeUsersGauge.setValue(metrics.activeUserCount);
            orderSuccessRateGauge.setValue(metrics.orderSuccessRate);
            totalRevenueGauge.setValue(metrics.totalOrderAmount);

            // 记录订单金额分布
            if (metrics.totalOrderCount > 0) {
                double avgOrderAmount = (double) metrics.totalOrderAmount / metrics.totalOrderCount;
                orderAmountHistogram.observe(avgOrderAmount);
            }

            LOGGER.info("业务指标上报成功: 总订单={}, 成功订单={}, 活跃用户={}, 成功率={}%",
                metrics.totalOrderCount, metrics.successOrderCount,
                metrics.activeUserCount, String.format("%.2f", metrics.orderSuccessRate));

        } catch (Exception e) {
            LOGGER.error("上报业务指标时发生错误", e);
        }
    }

    @Override
    public int priority() {
        return 1; // 较低优先级，确保在其他核心服务之后启动
    }
}
