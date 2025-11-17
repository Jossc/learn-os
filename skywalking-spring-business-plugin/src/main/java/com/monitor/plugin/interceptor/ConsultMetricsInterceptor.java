package com.monitor.plugin.interceptor;

import com.monitor.plugin.metric.BusinessMetricRegistry;
import com.monitor.plugin.metric.MetricConstants;
import org.apache.skywalking.apm.agent.core.meter.Counter;
import org.apache.skywalking.apm.agent.core.meter.Histogram;
import org.apache.skywalking.apm.agent.core.meter.Gauge;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在线问诊指标拦截器
 * 监控问诊相关的业务指标
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class ConsultMetricsInterceptor implements MethodInterceptor {

    private static final ILog logger = LogManager.getLogger(ConsultMetricsInterceptor.class);

    private final BusinessMetricRegistry metricRegistry;
    private final AtomicInteger activeConsultCount = new AtomicInteger(0);
    private final Map<String, Long> consultStartTimes = new ConcurrentHashMap<>();

    public ConsultMetricsInterceptor(BusinessMetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        Object[] arguments = invocation.getArguments();
        long startTime = System.currentTimeMillis();

        // 提取业务上下文
        Map<String, String> businessContext = extractBusinessContext(methodName, arguments);
        String consultId = businessContext.get("consult_id");

        try {
            // 前置处理
            handleBeforeMethod(methodName, businessContext);

            // 执行原方法
            Object result = invocation.proceed();

            // 后置处理
            handleAfterMethod(methodName, businessContext, result, startTime);

            return result;

        } catch (Throwable throwable) {
            // 异常处理
            handleException(methodName, businessContext, throwable, startTime);
            throw throwable;
        } finally {
            // 清理资源
            if (consultId != null) {
                consultStartTimes.remove(consultId);
            }
        }
    }

    /**
     * 前置方法处理
     */
    private void handleBeforeMethod(String methodName, Map<String, String> context) {
        try {
            switch (methodName) {
                case "startConsult":
                    recordConsultStarted(context);
                    break;
                case "completeConsult":
                    // 完成问诊不需要前置处理
                    break;
                case "cancelConsult":
                    // 取消问诊不需要前置处理
                    break;
                case "prescribeConsult":
                    // 开处方不需要前置处理
                    break;
                default:
                    logger.debug("未识别的问诊方法: {}", methodName);
            }
        } catch (Exception e) {
            logger.error("问诊前置处理异常: " + methodName, e);
        }
    }

    /**
     * 后置方法处理
     */
    private void handleAfterMethod(String methodName, Map<String, String> context,
                                  Object result, long startTime) {
        try {
            long duration = System.currentTimeMillis() - startTime;

            switch (methodName) {
                case "startConsult":
                    // 记录问诊开始成功
                    break;
                case "completeConsult":
                    recordConsultCompleted(context, duration);
                    break;
                case "cancelConsult":
                    recordConsultCancelled(context, duration);
                    break;
                case "prescribeConsult":
                    recordConsultPrescribed(context, duration);
                    break;
            }

        } catch (Exception e) {
            logger.error("问诊后置处理异常: " + methodName, e);
        }
    }

    /**
     * 异常处理
     */
    private void handleException(String methodName, Map<String, String> context,
                                Throwable throwable, long startTime) {
        try {
            long duration = System.currentTimeMillis() - startTime;

            // 记录问诊失败
            Counter failedCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Consult.STARTED.replace("started", "failed"), Counter.class);

            failedCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.REASON, throwable.getClass().getSimpleName(),
                "method", methodName);

            // 记录异常持续时间
            Histogram durationHistogram = metricRegistry.getOrCreateMetric(
                MetricConstants.Consult.DURATION + ".failed", Histogram.class);
            durationHistogram.observe(duration);

            // 减少活跃问诊计数
            if ("startConsult".equals(methodName)) {
                activeConsultCount.decrementAndGet();
                updateActiveConsultGauge();
            }

            logger.warn("问诊方法执行异常: {} - {}", methodName, throwable.getMessage());

        } catch (Exception e) {
            logger.error("问诊异常处理失败: " + methodName, e);
        }
    }

    /**
     * 记录问诊开始
     */
    private void recordConsultStarted(Map<String, String> context) {
        try {
            // 增加问诊开始计数
            Counter startedCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Consult.STARTED, Counter.class);

            startedCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.DOCTOR_ID, context.getOrDefault("doctor_id", "unknown"),
                MetricConstants.Tags.SPECIALTY, context.getOrDefault("specialty", "unknown"));

            // 增加活跃问诊计数
            activeConsultCount.incrementAndGet();
            updateActiveConsultGauge();

            // 记录问诊开始时间
            String consultId = context.get("consult_id");
            if (consultId != null) {
                consultStartTimes.put(consultId, System.currentTimeMillis());
            }

            logger.debug("记录问诊开始: {}", context.get("consult_id"));

        } catch (Exception e) {
            logger.error("记录问诊开始失败", e);
        }
    }

    /**
     * 记录问诊完成
     */
    private void recordConsultCompleted(Map<String, String> context, long methodDuration) {
        try {
            String consultId = context.get("consult_id");

            // 增加问诊完成计数
            Counter completedCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Consult.COMPLETED, Counter.class);

            completedCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.DOCTOR_ID, context.getOrDefault("doctor_id", "unknown"),
                MetricConstants.Tags.RESULT, MetricConstants.TagValues.CONSULT_SUCCESS);

            // 记录问诊总持续时间
            if (consultId != null) {
                Long startTime = consultStartTimes.get(consultId);
                if (startTime != null) {
                    long totalDuration = System.currentTimeMillis() - startTime;

                    Histogram durationHistogram = metricRegistry.getOrCreateMetric(
                        MetricConstants.Consult.DURATION, Histogram.class);
                    durationHistogram.observe(totalDuration);
                }
            }

            // 减少活跃问诊计数
            activeConsultCount.decrementAndGet();
            updateActiveConsultGauge();

            logger.debug("记录问诊完成: {}", consultId);

        } catch (Exception e) {
            logger.error("记录问诊完成失败", e);
        }
    }

    /**
     * 记录问诊取消
     */
    private void recordConsultCancelled(Map<String, String> context, long methodDuration) {
        try {
            // 增加问诊取消计数
            Counter cancelledCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Consult.CANCELLED, Counter.class);

            cancelledCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.REASON, context.getOrDefault("cancel_reason", "unknown"));

            // 减少活跃问诊计数
            activeConsultCount.decrementAndGet();
            updateActiveConsultGauge();

            logger.debug("记录问诊取消: {}", context.get("consult_id"));

        } catch (Exception e) {
            logger.error("记录问诊取消失败", e);
        }
    }

    /**
     * 记录问诊开处方
     */
    private void recordConsultPrescribed(Map<String, String> context, long methodDuration) {
        try {
            // 增加处方率统计
            Counter prescriptionCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Consult.PRESCRIPTION_RATE, Counter.class);

            prescriptionCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.DOCTOR_ID, context.getOrDefault("doctor_id", "unknown"));

            logger.debug("记录问诊开处方: {}", context.get("consult_id"));

        } catch (Exception e) {
            logger.error("记录问诊开处方失败", e);
        }
    }

    /**
     * 更新活跃问诊数量指标
     */
    private void updateActiveConsultGauge() {
        try {
            Gauge activeGauge = metricRegistry.getOrCreateMetric(
                MetricConstants.Consult.ACTIVE_COUNT, Gauge.class);
            activeGauge.setValue(activeConsultCount.get());
        } catch (Exception e) {
            logger.error("更新活跃问诊数量失败", e);
        }
    }

    /**
     * 提取业务上下文信息
     */
    private Map<String, String> extractBusinessContext(String methodName, Object[] arguments) {
        Map<String, String> context = new ConcurrentHashMap<>();

        try {
            // 根据方法名和参数提取业务信息
            if (arguments != null && arguments.length > 0) {
                // 假设第一个参数包含问诊ID或相关信息
                Object firstArg = arguments[0];
                if (firstArg != null) {
                    // 通过反射或约定提取业务信息
                    context.put("consult_id", extractConsultId(firstArg));
                    context.put("doctor_id", extractDoctorId(firstArg));
                    context.put("specialty", extractSpecialty(firstArg));
                    context.put("service", "consult-service");
                }
            }
        } catch (Exception e) {
            logger.warn("提取业务上下文失败: " + methodName, e);
        }

        return context;
    }

    private String extractConsultId(Object arg) {
        // 实际实现中应该根据具体的参数类型来提取
        return "consult_" + arg.hashCode();
    }

    private String extractDoctorId(Object arg) {
        // 实际实现中应该根据具体的参数类型来提取
        return "doctor_" + Math.abs(arg.hashCode() % 1000);
    }

    private String extractSpecialty(Object arg) {
        // 实际实现中应该根据具体的参数类型来提取
        String[] specialties = {"内科", "外科", "儿科", "妇科", "皮肤科"};
        return specialties[Math.abs(arg.hashCode() % specialties.length)];
    }
}
