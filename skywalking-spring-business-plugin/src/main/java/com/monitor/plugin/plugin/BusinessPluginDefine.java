package com.monitor.plugin.plugin;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.apache.skywalking.apm.agent.core.plugin.match.NameMatch.byName;
import static org.apache.skywalking.apm.agent.core.plugin.match.MultiClassNameMatch.byMultiClassMatch;

/**
 * SkyWalking业务插件定义
 * 定义需要拦截的业务类和方法
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class BusinessPluginDefine extends ClassInstanceMethodsEnhancePluginDefine {

    // 需要增强的业务类
    private static final String[] ENHANCE_CLASSES = {
        "com.medical.consult.service.ConsultService",
        "com.medical.prescription.service.PrescriptionService",
        "com.medical.order.service.OrderService",
        "com.medical.workorder.service.WorkorderService"
    };

    // 拦截器类映射
    private static final String CONSULT_INTERCEPTOR = "com.monitor.plugin.interceptor.ConsultMetricsInterceptor";
    private static final String PRESCRIPTION_INTERCEPTOR = "com.monitor.plugin.interceptor.PrescriptionMetricsInterceptor";
    private static final String ORDER_INTERCEPTOR = "com.monitor.plugin.interceptor.OrderMetricsInterceptor";
    private static final String WORKORDER_INTERCEPTOR = "com.monitor.plugin.interceptor.WorkorderMetricsInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return byMultiClassMatch(ENHANCE_CLASSES);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            // 问诊服务方法拦截
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("startConsult")
                        .or(named("completeConsult"))
                        .or(named("cancelConsult"))
                        .or(named("prescribeConsult"));
                }

                @Override
                public String getMethodsInterceptor() {
                    return CONSULT_INTERCEPTOR;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            },

            // 处方服务方法拦截
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("createPrescription")
                        .or(named("validatePrescription"))
                        .or(named("dispensePrescription"))
                        .or(named("checkDrugInteraction"));
                }

                @Override
                public String getMethodsInterceptor() {
                    return PRESCRIPTION_INTERCEPTOR;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            },

            // 订单服务方法拦截
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("createOrder")
                        .or(named("payOrder"))
                        .or(named("deliverOrder"))
                        .or(named("cancelOrder"))
                        .or(named("refundOrder"));
                }

                @Override
                public String getMethodsInterceptor() {
                    return ORDER_INTERCEPTOR;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            },

            // 工单服务方法拦截
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("createWorkorder")
                        .or(named("assignWorkorder"))
                        .or(named("resolveWorkorder"))
                        .or(named("closeWorkorder"));
                }

                @Override
                public String getMethodsInterceptor() {
                    return WORKORDER_INTERCEPTOR;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}
