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
 * 工单指标拦截器
 * 监控工单相关的业务指标
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class WorkorderMetricsInterceptor implements MethodInterceptor {

    private static final ILog logger = LogManager.getLogger(WorkorderMetricsInterceptor.class);

    private final BusinessMetricRegistry metricRegistry;
    private final AtomicInteger pendingWorkorderCount = new AtomicInteger(0);
    private final Map<String, Long> workorderStartTimes = new ConcurrentHashMap<>();

    public WorkorderMetricsInterceptor(BusinessMetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        Object[] arguments = invocation.getArguments();
        long startTime = System.currentTimeMillis();

        Map<String, String> businessContext = extractBusinessContext(methodName, arguments);
        String workorderId = businessContext.get("workorder_id");

        try {
            handleBeforeMethod(methodName, businessContext);
            Object result = invocation.proceed();
            handleAfterMethod(methodName, businessContext, result, startTime);
            return result;

        } catch (Throwable throwable) {
            handleException(methodName, businessContext, throwable, startTime);
            throw throwable;
        } finally {
            if (workorderId != null && "resolveWorkorder".equals(methodName)) {
                workorderStartTimes.remove(workorderId);
            }
        }
    }

    private void handleBeforeMethod(String methodName, Map<String, String> context) {
        try {
            switch (methodName) {
                case "createWorkorder":
                    recordWorkorderCreated(context);
                    break;
                case "assignWorkorder":
                    recordWorkorderAssigned(context);
                    break;
            }
        } catch (Exception e) {
            logger.error("工单前置处理异常: " + methodName, e);
        }
    }

    private void handleAfterMethod(String methodName, Map<String, String> context, Object result, long startTime) {
        try {
            long duration = System.currentTimeMillis() - startTime;

            switch (methodName) {
                case "createWorkorder":
                    recordWorkorderCreationCompleted(context, duration);
                    break;
                case "assignWorkorder":
                    recordWorkorderAssignmentCompleted(context, duration);
                    break;
                case "resolveWorkorder":
                    recordWorkorderResolved(context, duration);
                    break;
                case "closeWorkorder":
                    recordWorkorderClosed(context, duration);
                    break;
            }
        } catch (Exception e) {
            logger.error("工单后置处理异常: " + methodName, e);
        }
    }

    private void handleException(String methodName, Map<String, String> context, Throwable throwable, long startTime) {
        try {
            Counter failedCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Workorder.CREATED.replace("created", "failed"), Counter.class);

            failedCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.CATEGORY, context.getOrDefault("category", "unknown"),
                MetricConstants.Tags.REASON, throwable.getClass().getSimpleName());

            logger.warn("工单方法执行异常: {} - {}", methodName, throwable.getMessage());

        } catch (Exception e) {
            logger.error("工单异常处理失败: " + methodName, e);
        }
    }

    /**
     * 记录工单创建
     */
    private void recordWorkorderCreated(Map<String, String> context) {
        try {
            Counter createdCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Workorder.CREATED, Counter.class);

            createdCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.CATEGORY, context.getOrDefault("category", "unknown"),
                MetricConstants.Tags.PRIORITY, context.getOrDefault("priority", "unknown"));

            // 增加待处理工单计数
            pendingWorkorderCount.incrementAndGet();
            updatePendingWorkorderGauge(context);

            // 记录工单创建时间
            String workorderId = context.get("workorder_id");
            if (workorderId != null) {
                workorderStartTimes.put(workorderId, System.currentTimeMillis());
            }

            logger.debug("记录工单创建: {}", workorderId);

        } catch (Exception e) {
            logger.error("记录工单创建失败", e);
        }
    }

    /**
     * 记录工单分配
     */
    private void recordWorkorderAssigned(Map<String, String> context) {
        try {
            Counter assignedCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Workorder.ASSIGNED, Counter.class);

            assignedCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.ASSIGNEE_TYPE, context.getOrDefault("assignee_type", "unknown"),
                MetricConstants.Tags.PRIORITY, context.getOrDefault("priority", "unknown"));

            logger.debug("记录工单分配: {}", context.get("workorder_id"));

        } catch (Exception e) {
            logger.error("记录工单分配失败", e);
        }
    }

    /**
     * 记录工单解决
     */
    private void recordWorkorderResolved(Map<String, String> context, long methodDuration) {
        try {
            String workorderId = context.get("workorder_id");

            Counter resolvedCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Workorder.RESOLVED, Counter.class);

            resolvedCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.RESOLUTION_TYPE, context.getOrDefault("resolution_type", "unknown"),
                MetricConstants.Tags.CATEGORY, context.getOrDefault("category", "unknown"));

            // 记录工单解决时间
            if (workorderId != null) {
                Long startTime = workorderStartTimes.get(workorderId);
                if (startTime != null) {
                    long totalResolutionTime = System.currentTimeMillis() - startTime;

                    Histogram resolutionTimeHistogram = metricRegistry.getOrCreateMetric(
                        MetricConstants.Workorder.RESOLUTION_TIME, Histogram.class);
                    resolutionTimeHistogram.observe(totalResolutionTime);

                    // 检查SLA违规
                    checkSLABreach(context, totalResolutionTime);
                }
            }

            // 减少待处理工单计数
            pendingWorkorderCount.decrementAndGet();
            updatePendingWorkorderGauge(context);

            logger.debug("记录工单解决: {}", workorderId);

        } catch (Exception e) {
            logger.error("记录工单解决失败", e);
        }
    }

    /**
     * 记录工单关闭
     */
    private void recordWorkorderClosed(Map<String, String> context, long methodDuration) {
        try {
            Counter closedCounter = metricRegistry.getOrCreateMetric(
                MetricConstants.Workorder.CLOSED, Counter.class);

            closedCounter.increment(1,
                MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                MetricConstants.Tags.CATEGORY, context.getOrDefault("category", "unknown"));

            logger.debug("记录工单关闭: {}", context.get("workorder_id"));

        } catch (Exception e) {
            logger.error("记录工单关闭失败", e);
        }
    }

    private void recordWorkorderCreationCompleted(Map<String, String> context, long duration) {
        // 可以记录工单创建完成的处理时间等指标
    }

    private void recordWorkorderAssignmentCompleted(Map<String, String> context, long duration) {
        // 可以记录工单分配完成的处理时间等指标
    }

    /**
     * 检查SLA违规
     */
    private void checkSLABreach(Map<String, String> context, long resolutionTime) {
        try {
            String priority = context.get("priority");
            long slaThreshold = getSLAThreshold(priority);

            if (resolutionTime > slaThreshold) {
                Counter slaBreachCounter = metricRegistry.getOrCreateMetric(
                    MetricConstants.Workorder.SLA_BREACH, Counter.class);

                slaBreachCounter.increment(1,
                    MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
                    MetricConstants.Tags.PRIORITY, priority != null ? priority : "unknown",
                    MetricConstants.Tags.CATEGORY, context.getOrDefault("category", "unknown"));

                logger.warn("工单SLA违规: {} - 解决时间: {}ms, SLA: {}ms",
                    context.get("workorder_id"), resolutionTime, slaThreshold);
            }
        } catch (Exception e) {
            logger.error("SLA检查失败", e);
        }
    }

    /**
     * 获取SLA阈值（毫秒）
     */
    private long getSLAThreshold(String priority) {
        if (priority == null) return 24 * 60 * 60 * 1000; // 默认24小时

        switch (priority.toLowerCase()) {
            case "urgent": return 2 * 60 * 60 * 1000;     // 2小时
            case "high": return 8 * 60 * 60 * 1000;       // 8小时
            case "medium": return 24 * 60 * 60 * 1000;    // 24小时
            case "low": return 72 * 60 * 60 * 1000;       // 72小时
            default: return 24 * 60 * 60 * 1000;          // 默认24小时
        }
    }

    /**
     * 更新待处理工单数量指标
     */
    private void updatePendingWorkorderGauge(Map<String, String> context) {
        try {
            Gauge pendingGauge = metricRegistry.getOrCreateMetric(
                MetricConstants.Workorder.PENDING_COUNT, Gauge.class);
            pendingGauge.setValue(pendingWorkorderCount.get());
        } catch (Exception e) {
            logger.error("更新待处理工单数量失败", e);
        }
    }

    /**
     * 提取业务上下文信息
     */
    private Map<String, String> extractBusinessContext(String methodName, Object[] arguments) {
        Map<String, String> context = new ConcurrentHashMap<>();

        try {
            if (arguments != null && arguments.length > 0) {
                Object firstArg = arguments[0];
                if (firstArg != null) {
                    context.put("workorder_id", extractWorkorderId(firstArg));
                    context.put("category", extractCategory(firstArg));
                    context.put("priority", extractPriority(firstArg));
                    context.put("assignee_type", extractAssigneeType(firstArg));
                    context.put("service", "workorder-service");
                }
            }
        } catch (Exception e) {
            logger.warn("提取工单业务上下文失败: " + methodName, e);
        }

        return context;
    }

    private String extractWorkorderId(Object arg) {
        return "workorder_" + arg.hashCode();
    }

    private String extractCategory(Object arg) {
        String[] categories = {
            MetricConstants.TagValues.CATEGORY_TECHNICAL,
            MetricConstants.TagValues.CATEGORY_BUSINESS,
            MetricConstants.TagValues.CATEGORY_COMPLAINT,
            MetricConstants.TagValues.CATEGORY_CONSULTATION
        };
        return categories[Math.abs(arg.hashCode() % categories.length)];
    }

    private String extractPriority(Object arg) {
        String[] priorities = {
            MetricConstants.TagValues.PRIORITY_LOW,
            MetricConstants.TagValues.PRIORITY_MEDIUM,
            MetricConstants.TagValues.PRIORITY_HIGH,
            MetricConstants.TagValues.PRIORITY_URGENT
        };
        return priorities[Math.abs(arg.hashCode() % priorities.length)];
    }

    private String extractAssigneeType(Object arg) {
        String[] assigneeTypes = {"engineer", "specialist", "manager", "support"};
        return assigneeTypes[Math.abs(arg.hashCode() % assigneeTypes.length)];
    }
}
