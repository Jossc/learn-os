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
 * 处方指标拦截器
 * 监控处方相关的业务指标
 */
public class PrescriptionMetricsInterceptor implements MethodInterceptor {

    private static final ILog logger = LogManager.getLogger(PrescriptionMetricsInterceptor.class);
    private final BusinessMetricRegistry metricRegistry;

    public PrescriptionMetricsInterceptor(BusinessMetricRegistry metricRegistry) {
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
        // 处方前置处理逻辑
        if ("createPrescription".equals(methodName)) {
            recordPrescriptionCreated(context);
        }
    }

    private void handleAfterMethod(String methodName, Map<String, String> context, Object result, long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        switch (methodName) {
            case "validatePrescription":
                recordPrescriptionValidated(context, duration);
                break;
            case "dispensePrescription":
                recordPrescriptionDispensed(context, duration);
                break;
            case "checkDrugInteraction":
                recordDrugInteractionCheck(context, duration);
                break;
        }
    }

    private void handleException(String methodName, Map<String, String> context, Throwable throwable, long startTime) {
        Counter failedCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Prescription.CREATED.replace("created", "failed"), Counter.class);

        failedCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
            MetricConstants.Tags.REASON, throwable.getClass().getSimpleName());
    }

    private void recordPrescriptionCreated(Map<String, String> context) {
        Counter createdCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Prescription.CREATED, Counter.class);

        createdCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
            MetricConstants.Tags.DOCTOR_ID, context.getOrDefault("doctor_id", "unknown"),
            MetricConstants.Tags.DRUG_TYPE, context.getOrDefault("drug_type", "unknown"));
    }

    private void recordPrescriptionValidated(Map<String, String> context, long duration) {
        Counter validatedCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Prescription.VALIDATED, Counter.class);

        validatedCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
            MetricConstants.Tags.VALIDATION_RESULT, context.getOrDefault("validation_result", "unknown"));

        Histogram validationTimeHistogram = metricRegistry.getOrCreateMetric(
            MetricConstants.Prescription.VALIDATION_TIME, Histogram.class);
        validationTimeHistogram.observe(duration);
    }

    private void recordPrescriptionDispensed(Map<String, String> context, long duration) {
        Counter dispensedCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Prescription.DISPENSED, Counter.class);

        dispensedCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"));
    }

    private void recordDrugInteractionCheck(Map<String, String> context, long duration) {
        Counter interactionCounter = metricRegistry.getOrCreateMetric(
            MetricConstants.Prescription.DRUG_INTERACTION_CHECK, Counter.class);

        interactionCounter.increment(1,
            MetricConstants.Tags.SERVICE, context.getOrDefault("service", "unknown"),
            MetricConstants.Tags.INTERACTION_FOUND, context.getOrDefault("interaction_found", "false"));
    }

    private Map<String, String> extractBusinessContext(String methodName, Object[] arguments) {
        Map<String, String> context = new ConcurrentHashMap<>();

        if (arguments != null && arguments.length > 0) {
            Object firstArg = arguments[0];
            if (firstArg != null) {
                context.put("prescription_id", "prescription_" + firstArg.hashCode());
                context.put("doctor_id", "doctor_" + Math.abs(firstArg.hashCode() % 1000));
                context.put("drug_type", extractDrugType(firstArg));
                context.put("service", "prescription-service");
            }
        }

        return context;
    }

    private String extractDrugType(Object arg) {
        String[] drugTypes = {"西药", "中药", "生物制品", "疫苗"};
        return drugTypes[Math.abs(arg.hashCode() % drugTypes.length)];
    }
}
