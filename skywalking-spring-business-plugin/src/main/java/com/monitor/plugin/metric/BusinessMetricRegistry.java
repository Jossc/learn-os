package com.monitor.plugin.metric;

import org.apache.skywalking.apm.agent.core.meter.*;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.conf.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务指标注册器
 * 基于SkyWalking MeterService管理所有业务指标
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class BusinessMetricRegistry {

    private static final ILog logger = LogManager.getLogger(BusinessMetricRegistry.class);

    private final MeterService meterService;
    private final Map<String, Meter> registeredMetrics;
    private volatile boolean initialized = false;

    public BusinessMetricRegistry() {
        this.meterService = MeterService.getInstance();
        this.registeredMetrics = new ConcurrentHashMap<>();
    }

    /**
     * 初始化业务指标定义
     */
    public void initializeBusinessMetrics() {
        if (initialized) {
            return;
        }

        try {
            logger.info("初始化业务指标定义...");

            // 问诊相关指标
            registerConsultMetrics();

            // 处方相关指标
            registerPrescriptionMetrics();

            // 订单相关指标
            registerOrderMetrics();

            // 工单相关指标
            registerWorkorderMetrics();

            this.initialized = true;
            logger.info("业务指标定义初始化完成，共注册 {} 个指标", registeredMetrics.size());

        } catch (Exception e) {
            logger.error("业务指标初始化失败", e);
            throw new RuntimeException("Failed to initialize business metrics", e);
        }
    }

    /**
     * 注册问诊相关指标
     */
    private void registerConsultMetrics() {
        // 问诊开始计数器
        registerCounter(MetricConstants.CONSULT_PREFIX + ".started",
            "问诊开始次数", "service", "doctor_id", "specialty");

        // 问诊完成计数器
        registerCounter(MetricConstants.CONSULT_PREFIX + ".completed",
            "问诊完成次数", "service", "doctor_id", "result");

        // 问诊取消计数器
        registerCounter(MetricConstants.CONSULT_PREFIX + ".cancelled",
            "问诊取消次数", "service", "reason");

        // 问诊持续时间直方图
        registerHistogram(MetricConstants.CONSULT_PREFIX + ".duration",
            "问诊持续时间分布", "service", "doctor_id", "complexity");

        // 当前活跃问诊数量
        registerGauge(MetricConstants.CONSULT_PREFIX + ".active.count",
            "当前活跃问诊数量", "service");
    }

    /**
     * 注册处方相关指标
     */
    private void registerPrescriptionMetrics() {
        // 处方创建计数器
        registerCounter(MetricConstants.PRESCRIPTION_PREFIX + ".created",
            "处方创建次数", "service", "doctor_id", "drug_type");

        // 处方验证计数器
        registerCounter(MetricConstants.PRESCRIPTION_PREFIX + ".validated",
            "处方验证次数", "service", "validation_result");

        // 药物相互作用检查计数器
        registerCounter(MetricConstants.PRESCRIPTION_PREFIX + ".drug_interaction_check",
            "药物相互作用检查次数", "service", "interaction_found");

        // 处方金额直方图
        registerHistogram(MetricConstants.PRESCRIPTION_PREFIX + ".amount",
            "处方金额分布", "service", "drug_category");
    }

    /**
     * 注册订单相关指标
     */
    private void registerOrderMetrics() {
        // 订单创建计数器
        registerCounter(MetricConstants.ORDER_PREFIX + ".created",
            "订单创建次数", "service", "order_type", "channel");

        // 订单支付计数器
        registerCounter(MetricConstants.ORDER_PREFIX + ".paid",
            "订单支付次数", "service", "payment_method");

        // 订单配送计数器
        registerCounter(MetricConstants.ORDER_PREFIX + ".delivered",
            "订单配送次数", "service", "delivery_type");

        // 订单金额直方图
        registerHistogram(MetricConstants.ORDER_PREFIX + ".amount",
            "订单金额分布", "service", "order_type");

        // 订单处理时间直方图
        registerHistogram(MetricConstants.ORDER_PREFIX + ".processing_time",
            "订单处理时间分布", "service", "complexity");
    }

    /**
     * 注册工单相关指标
     */
    private void registerWorkorderMetrics() {
        // 工单创建计数器
        registerCounter(MetricConstants.WORKORDER_PREFIX + ".created",
            "工单创建次数", "service", "category", "priority");

        // 工单分配计数器
        registerCounter(MetricConstants.WORKORDER_PREFIX + ".assigned",
            "工单分配次数", "service", "assignee_type");

        // 工单解决计数器
        registerCounter(MetricConstants.WORKORDER_PREFIX + ".resolved",
            "工单解决次数", "service", "resolution_type");

        // 工单解决时间直方图
        registerHistogram(MetricConstants.WORKORDER_PREFIX + ".resolution_time",
            "工单解决时间分布", "service", "priority", "category");

        // 未解决工单数量
        registerGauge(MetricConstants.WORKORDER_PREFIX + ".pending.count",
            "未解决工单数量", "service", "priority");
    }

    /**
     * 注册计数器指标
     */
    public Counter registerCounter(String name, String description, String... tags) {
        try {
            Counter counter = meterService.getCounter(name)
                .tag("service", Config.Agent.SERVICE_NAME);

            // 添加额外标签
            for (String tag : tags) {
                counter = counter.tag(tag, "");
            }

            Counter builtCounter = counter.build();
            registeredMetrics.put(name, builtCounter);

            logger.debug("注册计数器指标: {} - {}", name, description);
            return builtCounter;

        } catch (Exception e) {
            logger.error("注册计数器指标失败: " + name, e);
            throw e;
        }
    }

    /**
     * 注册计量器指标
     */
    public Gauge registerGauge(String name, String description, String... tags) {
        try {
            Gauge gauge = meterService.getGauge(name)
                .tag("service", Config.Agent.SERVICE_NAME);

            // 添加额外标签
            for (String tag : tags) {
                gauge = gauge.tag(tag, "");
            }

            Gauge builtGauge = gauge.build();
            registeredMetrics.put(name, builtGauge);

            logger.debug("注册计量器指标: {} - {}", name, description);
            return builtGauge;

        } catch (Exception e) {
            logger.error("注册计量器指标失败: " + name, e);
            throw e;
        }
    }

    /**
     * 注册直方图指标
     */
    public Histogram registerHistogram(String name, String description, String... tags) {
        try {
            Histogram histogram = meterService.getHistogram(name)
                .tag("service", Config.Agent.SERVICE_NAME)
                .steps(10, 50, 100, 500, 1000, 5000); // 默认桶配置

            // 添加额外标签
            for (String tag : tags) {
                histogram = histogram.tag(tag, "");
            }

            Histogram builtHistogram = histogram.build();
            registeredMetrics.put(name, builtHistogram);

            logger.debug("注册直方图指标: {} - {}", name, description);
            return builtHistogram;

        } catch (Exception e) {
            logger.error("注册直方图指标失败: " + name, e);
            throw e;
        }
    }

    /**
     * 获取或创建指标
     */
    @SuppressWarnings("unchecked")
    public <T extends Meter> T getOrCreateMetric(String name, Class<T> metricType) {
        Meter metric = registeredMetrics.get(name);
        if (metric != null && metricType.isInstance(metric)) {
            return (T) metric;
        }

        // 如果指标不存在，动态创建
        if (metricType == Counter.class) {
            return (T) registerCounter(name, "动态创建的计数器");
        } else if (metricType == Gauge.class) {
            return (T) registerGauge(name, "动态创建的计量器");
        } else if (metricType == Histogram.class) {
            return (T) registerHistogram(name, "动态创建的直方图");
        }

        throw new IllegalArgumentException("不支持的指标类型: " + metricType);
    }

    /**
     * 启动指标收集
     */
    public void startMetricCollection() {
        logger.info("开始收集业务指标数据");
        // 这里可以添加定期任务，比如清理过期数据等
    }

    /**
     * 获取已注册的指标数量
     */
    public int getRegisteredMetricCount() {
        return registeredMetrics.size();
    }

    /**
     * 关闭指标注册器
     */
    public void shutdown() {
        logger.info("关闭业务指标注册器...");

        try {
            // 清理资源
            registeredMetrics.clear();
            this.initialized = false;

            logger.info("业务指标注册器已关闭");
        } catch (Exception e) {
            logger.error("关闭业务指标注册器异常", e);
        }
    }
}
