package com.monitor.plugin.interceptor;

import com.monitor.plugin.metric.BusinessMetricRegistry;
import com.monitor.plugin.metric.MetricConstants;
import org.apache.skywalking.apm.agent.core.meter.Counter;
import org.apache.skywalking.apm.agent.core.meter.Histogram;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单指标拦截器
 * 监控订单相关的业务指标
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class OrderMetricsInterceptor implements MethodInterceptor {

    private static final ILog logger = LogManager.getLogger(OrderMetricsInterceptor.class);

    private final BusinessMetricRegistry metricRegistry;

    public OrderMetricsInterceptor(BusinessMetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        Object[] arguments = invocation.getArguments();
        long startTime = System.currentTimeMillis();

        Map<String, String> businessContext = extractBusinessContext(methodName, arguments);

        try {
            handleBeforeMethod(methodName, businessContext);
            Object result = invocation.proceed();
            handleAfterMethod(methodName, businessContext, result, startTime);
            return result;

        } catch (Throwable throwable) {
            handleException(methodName, businessContext, throwable, startTime);
            throw throwable;
        }
    }

    private void handleBeforeMethod(String methodName, Map<String, String> context) {
        try {
            switch (methodName) {
                case "createOrder":
                    recordOrderCreated(context);
                    break;
                case "payOrder":
                    recordOrderPaymentStarted(context);
                    break;
            }
        } catch (Exception e) {
            logger.error("订单前置处理异常: " + methodName, e);
        }
    }

    private void handleAfterMethod(String methodName, Map<String, String> context,
                                  Object result, long startTime) {
        try {
            long duration = System.currentTimeMillis() - startTime;

            switch (methodName) {
                case "createOrder":
                    recordOrderCreationCompleted(context, duration);
                    break;
                case "payOrder":
                    recordOrderPaid(context, duration);
                    break;
                case "deliverOrder":
                    recordOrderDelivered(context, duration);
                    break;
                case "cancelOrder":
                    recordOrderCancelled(context, duration);
                    break;
                case "refundOrder":
                    recordOrderRefunded(context, duration);
                    break;
            }

        } catch (Exception e) {
            logger.error("订单后置处理异常: " + methodName, e);
        }
    }

    private void handleException(String methodName, Map<String, String> context,
                                Throwable throwable, long startTime) {
        try {
            Counter failedCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Order.CREATED.replace("created", "failed"), Counter.class);

            failedCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.ORDER_TYPE, context.getOrDefault("order_type", "unknown"),
                MetricConstants.Tags.REASON, throwable.getClass().getSimpleName());

            logger.warn("订单方法执行异常: {} - {}", methodName, throwable.getMessage());

        } catch (Exception e) {
            logger.error("订单异常处理失败: " + methodName, e);
        }
    }

    private void recordOrderCreated(Map<String, String> context) {
        Counter createdCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Order.CREATED, Counter.class);

        createdCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
            MetricConstants.Tags.ORDER_TYPE, context.getOrDefault("order_type", "unknown"),
            MetricConstants.Tags.CHANNEL, context.getOrDefault("channel", "unknown"));

        // 记录订单金额
        String amountStr = context.get("amount");
        if (amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                Histogram amountHistogram = metricRegistry.getOrCreateMetric(
                    MetricConstants.Order.AMOUNT, Histogram.class);
                amountHistogram.observe(amount);
            } catch (NumberFormatException e) {
                logger.warn("无效的订单金额: {}", amountStr);
            }
        }

        logger.debug("记录订单创建: {}", context.get("order_id"));
    }

    private void recordOrderCreationCompleted(Map<String, String> context, long duration) {
        Histogram processingTimeHistogram = metricRegistry.getOrCreateMetric(
            MetricConstants.Order.PROCESSING_TIME, Histogram.class);
        processingTimeHistogram.observe(duration);
    }

    private void recordOrderPaid(Map<String, String> context, long duration) {
        Counter paidCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Order.PAID, Counter.class);

        paidCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
            MetricConstants.Tags.PAYMENT_METHOD, context.getOrDefault("payment_method", "unknown"));

        logger.debug("记录订单支付: {}", context.get("order_id"));
    }

    private void recordOrderDelivered(Map<String, String> context, long duration) {
        Counter deliveredCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Order.DELIVERED, Counter.class);

        deliveredCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
            MetricConstants.Tags.DELIVERY_TYPE, context.getOrDefault("delivery_type", "unknown"));

        // 记录配送时间
        Histogram deliveryTimeHistogram = metricRegistry.getOrCreateMetric(
            MetricConstants.Order.DELIVERY_TIME, Histogram.class);
        deliveryTimeHistogram.observe(duration);

        logger.debug("记录订单配送: {}", context.get("order_id"));
    }

    private void recordOrderCancelled(Map<String, String> context, long duration) {
        Counter cancelledCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Order.CANCELLED, Counter.class);

        cancelledCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
            MetricConstants.Tags.REASON, context.getOrDefault("cancel_reason", "unknown"));

        logger.debug("记录订单取消: {}", context.get("order_id"));
    }

    private void recordOrderRefunded(Map<String, String> context, long duration) {
        Counter refundedCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Order.REFUNDED, Counter.class);

        refundedCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
            MetricConstants.Tags.REASON, context.getOrDefault("refund_reason", "unknown"));

        logger.debug("记录订单退款: {}", context.get("order_id"));
    }

    private void recordOrderPaymentStarted(Map<String, String> context) {
        // 可以记录支付开始的指标
        logger.debug("订单支付开始: {}", context.get("order_id"));
    }

    private Map<String, String> extractBusinessContext(String methodName, Object[] arguments) {
        Map<String, String> context = new ConcurrentHashMap<>();

        try {
            if (arguments != null && arguments.length > 0) {
                Object firstArg = arguments[0];
                if (firstArg != null) {
                    context.put("order_id", extractOrderId(firstArg));
                    context.put("order_type", extractOrderType(firstArg));
                    context.put("channel", extractChannel(firstArg));
                    context.put("amount", extractAmount(firstArg));
                    context.put("service", "order-service");
                }
            }
        } catch (Exception e) {
            logger.warn("提取订单业务上下文失败: " + methodName, e);
        }

        return context;
    }

    private String extractOrderId(Object arg) {
        return "order_" + arg.hashCode();
    }

    private String extractOrderType(Object arg) {
        String[] types = {MetricConstants.TagValues.ORDER_PRESCRIPTION,
                         MetricConstants.TagValues.ORDER_OTC,
                         MetricConstants.TagValues.ORDER_DEVICE};
        return types[Math.abs(arg.hashCode() % types.length)];
    }

    private String extractChannel(Object arg) {
        String[] channels = {MetricConstants.TagValues.CHANNEL_WEB,
                           MetricConstants.TagValues.CHANNEL_MOBILE,
                           MetricConstants.TagValues.CHANNEL_MINI_PROGRAM};
        return channels[Math.abs(arg.hashCode() % channels.length)];
    }

    private String extractAmount(Object arg) {
        // 模拟订单金额
        return String.valueOf(Math.abs(arg.hashCode() % 10000) + 100);
    }
}
