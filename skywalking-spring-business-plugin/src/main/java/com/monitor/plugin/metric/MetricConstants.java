package com.monitor.plugin.metric;

/**
 * 业务指标常量定义
 * 统一管理所有指标名称前缀和单位
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public final class MetricConstants {

    // 指标前缀定义
    public static final String CONSULT_PREFIX = "business.consult";
    public static final String PRESCRIPTION_PREFIX = "business.prescription";
    public static final String ORDER_PREFIX = "business.order";
    public static final String WORKORDER_PREFIX = "business.workorder";

    // 通用指标后缀
    public static final String STARTED_SUFFIX = ".started";
    public static final String COMPLETED_SUFFIX = ".completed";
    public static final String CANCELLED_SUFFIX = ".cancelled";
    public static final String FAILED_SUFFIX = ".failed";
    public static final String DURATION_SUFFIX = ".duration";
    public static final String COUNT_SUFFIX = ".count";
    public static final String AMOUNT_SUFFIX = ".amount";

    // 指标单位定义
    public static final String DURATION_UNIT = "ms";
    public static final String COUNT_UNIT = "count";
    public static final String AMOUNT_UNIT = "yuan";
    public static final String PERCENT_UNIT = "percent";
    public static final String RATE_UNIT = "rate";

    // 问诊业务指标名称
    public static final class Consult {
        public static final String STARTED = CONSULT_PREFIX + STARTED_SUFFIX;
        public static final String COMPLETED = CONSULT_PREFIX + COMPLETED_SUFFIX;
        public static final String CANCELLED = CONSULT_PREFIX + CANCELLED_SUFFIX;
        public static final String DURATION = CONSULT_PREFIX + DURATION_SUFFIX;
        public static final String ACTIVE_COUNT = CONSULT_PREFIX + ".active" + COUNT_SUFFIX;
        public static final String PRESCRIPTION_RATE = CONSULT_PREFIX + ".prescription.rate";
        public static final String SATISFACTION_SCORE = CONSULT_PREFIX + ".satisfaction.score";
    }

    // 处方业务指标名称
    public static final class Prescription {
        public static final String CREATED = PRESCRIPTION_PREFIX + ".created";
        public static final String VALIDATED = PRESCRIPTION_PREFIX + ".validated";
        public static final String DISPENSED = PRESCRIPTION_PREFIX + ".dispensed";
        public static final String DRUG_INTERACTION_CHECK = PRESCRIPTION_PREFIX + ".drug_interaction_check";
        public static final String AMOUNT = PRESCRIPTION_PREFIX + AMOUNT_SUFFIX;
        public static final String VALIDATION_TIME = PRESCRIPTION_PREFIX + ".validation.time";
        public static final String DRUG_COUNT = PRESCRIPTION_PREFIX + ".drug" + COUNT_SUFFIX;
    }

    // 订单业务指标名称
    public static final class Order {
        public static final String CREATED = ORDER_PREFIX + ".created";
        public static final String PAID = ORDER_PREFIX + ".paid";
        public static final String DELIVERED = ORDER_PREFIX + ".delivered";
        public static final String CANCELLED = ORDER_PREFIX + CANCELLED_SUFFIX;
        public static final String REFUNDED = ORDER_PREFIX + ".refunded";
        public static final String AMOUNT = ORDER_PREFIX + AMOUNT_SUFFIX;
        public static final String PROCESSING_TIME = ORDER_PREFIX + ".processing.time";
        public static final String DELIVERY_TIME = ORDER_PREFIX + ".delivery.time";
        public static final String SUCCESS_RATE = ORDER_PREFIX + ".success.rate";
    }

    // 工单业务指标名称
    public static final class Workorder {
        public static final String CREATED = WORKORDER_PREFIX + ".created";
        public static final String ASSIGNED = WORKORDER_PREFIX + ".assigned";
        public static final String RESOLVED = WORKORDER_PREFIX + ".resolved";
        public static final String CLOSED = WORKORDER_PREFIX + ".closed";
        public static final String ESCALATED = WORKORDER_PREFIX + ".escalated";
        public static final String RESOLUTION_TIME = WORKORDER_PREFIX + ".resolution.time";
        public static final String PENDING_COUNT = WORKORDER_PREFIX + ".pending" + COUNT_SUFFIX;
        public static final String SLA_BREACH = WORKORDER_PREFIX + ".sla.breach";
    }

    // 标签键定义
    public static final class Tags {
        public static final String SERVICE = "service";
        public static final String DOCTOR_ID = "doctor_id";
        public static final String SPECIALTY = "specialty";
        public static final String RESULT = "result";
        public static final String REASON = "reason";
        public static final String COMPLEXITY = "complexity";
        public static final String DRUG_TYPE = "drug_type";
        public static final String DRUG_CATEGORY = "drug_category";
        public static final String VALIDATION_RESULT = "validation_result";
        public static final String INTERACTION_FOUND = "interaction_found";
        public static final String ORDER_TYPE = "order_type";
        public static final String CHANNEL = "channel";
        public static final String PAYMENT_METHOD = "payment_method";
        public static final String DELIVERY_TYPE = "delivery_type";
        public static final String CATEGORY = "category";
        public static final String PRIORITY = "priority";
        public static final String ASSIGNEE_TYPE = "assignee_type";
        public static final String RESOLUTION_TYPE = "resolution_type";
    }

    // 标签值定义
    public static final class TagValues {
        // 问诊结果
        public static final String CONSULT_SUCCESS = "success";
        public static final String CONSULT_CANCELLED = "cancelled";
        public static final String CONSULT_TIMEOUT = "timeout";

        // 取消原因
        public static final String CANCELLED_BY_PATIENT = "patient_cancelled";
        public static final String CANCELLED_BY_DOCTOR = "doctor_cancelled";
        public static final String CANCELLED_BY_SYSTEM = "system_cancelled";

        // 复杂度
        public static final String COMPLEXITY_LOW = "low";
        public static final String COMPLEXITY_MEDIUM = "medium";
        public static final String COMPLEXITY_HIGH = "high";

        // 验证结果
        public static final String VALIDATION_PASSED = "passed";
        public static final String VALIDATION_FAILED = "failed";
        public static final String VALIDATION_WARNING = "warning";

        // 订单类型
        public static final String ORDER_PRESCRIPTION = "prescription";
        public static final String ORDER_OTC = "otc";
        public static final String ORDER_DEVICE = "device";

        // 渠道
        public static final String CHANNEL_WEB = "web";
        public static final String CHANNEL_MOBILE = "mobile";
        public static final String CHANNEL_MINI_PROGRAM = "mini_program";
        public static final String CHANNEL_API = "api";

        // 支付方式
        public static final String PAYMENT_ALIPAY = "alipay";
        public static final String PAYMENT_WECHAT = "wechat";
        public static final String PAYMENT_BANK_CARD = "bank_card";
        public static final String PAYMENT_INSURANCE = "insurance";

        // 工单优先级
        public static final String PRIORITY_LOW = "low";
        public static final String PRIORITY_MEDIUM = "medium";
        public static final String PRIORITY_HIGH = "high";
        public static final String PRIORITY_URGENT = "urgent";

        // 工单类别
        public static final String CATEGORY_TECHNICAL = "technical";
        public static final String CATEGORY_BUSINESS = "business";
        public static final String CATEGORY_COMPLAINT = "complaint";
        public static final String CATEGORY_CONSULTATION = "consultation";
    }

    // 私有构造函数，防止实例化
    private MetricConstants() {
        throw new AssertionError("不能实例化常量类");
    }
}
