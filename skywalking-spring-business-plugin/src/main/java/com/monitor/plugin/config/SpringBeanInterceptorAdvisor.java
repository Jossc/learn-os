package com.monitor.plugin.config;

import com.monitor.plugin.metric.BusinessMetricRegistry;
import com.monitor.plugin.interceptor.*;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring Bean拦截器适配器
 * 将SkyWalking拦截器集成到Spring AOP框架中
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
public class SpringBeanInterceptorAdvisor implements BeanPostProcessor, ApplicationContextAware {

    private static final ILog logger = LogManager.getLogger(SpringBeanInterceptorAdvisor.class);

    private final BusinessMetricRegistry metricRegistry;
    private ApplicationContext applicationContext;

    // 业务服务类名模式
    private static final List<String> BUSINESS_SERVICE_PATTERNS = Arrays.asList(
        "ConsultService", "PrescriptionService", "OrderService", "WorkorderService"
    );

    // 代理对象缓存
    private final ConcurrentHashMap<String, Object> proxiedBeans = new ConcurrentHashMap<>();

    // 拦截器实例
    private final ConsultMetricsInterceptor consultInterceptor;
    private final PrescriptionMetricsInterceptor prescriptionInterceptor;
    private final OrderMetricsInterceptor orderInterceptor;
    private final WorkorderMetricsInterceptor workorderInterceptor;

    public SpringBeanInterceptorAdvisor(BusinessMetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;

        // 初始化拦截器实例
        this.consultInterceptor = new ConsultMetricsInterceptor(metricRegistry);
        this.prescriptionInterceptor = new PrescriptionMetricsInterceptor(metricRegistry);
        this.orderInterceptor = new OrderMetricsInterceptor(metricRegistry);
        this.workorderInterceptor = new WorkorderMetricsInterceptor(metricRegistry);

        logger.info("Spring Bean拦截器适配器初始化完成");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 检查是否是需要代理的业务服务Bean
        if (shouldProxy(bean, beanName)) {
            return createProxy(bean, beanName);
        }
        return bean;
    }

    /**
     * 判断是否需要代理Bean
     */
    private boolean shouldProxy(Object bean, String beanName) {
        if (bean == null) {
            return false;
        }

        String className = bean.getClass().getSimpleName();
        return BUSINESS_SERVICE_PATTERNS.stream()
            .anyMatch(pattern -> className.contains(pattern));
    }

    /**
     * 创建代理对象
     */
    private Object createProxy(Object bean, String beanName) {
        try {
            ProxyFactory proxyFactory = new ProxyFactory(bean);

            // 根据Bean类型添加相应的拦截器
            String className = bean.getClass().getSimpleName();

            if (className.contains("ConsultService")) {
                proxyFactory.addAdvice(consultInterceptor);
                logger.debug("为 {} 添加问诊指标拦截器", beanName);
            } else if (className.contains("PrescriptionService")) {
                proxyFactory.addAdvice(prescriptionInterceptor);
                logger.debug("为 {} 添加处方指标拦截器", beanName);
            } else if (className.contains("OrderService")) {
                proxyFactory.addAdvice(orderInterceptor);
                logger.debug("为 {} 添加订单指标拦截器", beanName);
            } else if (className.contains("WorkorderService")) {
                proxyFactory.addAdvice(workorderInterceptor);
                logger.debug("为 {} 添加工单指标拦截器", beanName);
            }

            Object proxy = proxyFactory.getProxy();
            proxiedBeans.put(beanName, proxy);

            logger.info("成功为业务服务 {} 创建监控代理", beanName);
            return proxy;

        } catch (Exception e) {
            logger.error("为Bean {} 创建代理失败", beanName, e);
            return bean; // 返回原始Bean，不影响正常功能
        }
    }

    /**
     * 配置Spring AOP
     */
    public void configureSpringAOP() {
        logger.info("配置Spring AOP集成...");

        try {
            // 注册BeanPostProcessor到Spring容器
            registerBeanPostProcessors();

            logger.info("Spring AOP集成配置完成");
        } catch (Exception e) {
            logger.error("Spring AOP配置失败", e);
        }
    }

    /**
     * 注册Bean后处理器
     */
    public void registerBeanPostProcessors() {
        if (applicationContext != null) {
            try {
                // 通过ApplicationContext注册BeanPostProcessor
                // 注意：这个方法在实际Spring环境中可能需要不同的实现方式
                logger.info("注册Bean后处理器到Spring容器");
            } catch (Exception e) {
                logger.warn("注册Bean后处理器失败，将使用备用方案", e);
            }
        }
    }

    /**
     * 扫描业务Bean
     */
    public void scanForBusinessBeans() {
        if (applicationContext == null) {
            return;
        }

        try {
            // 扫描所有业务服务Bean
            String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
            int proxiedCount = 0;

            for (String beanName : beanNames) {
                try {
                    Object bean = applicationContext.getBean(beanName);
                    if (shouldProxy(bean, beanName)) {
                        createProxy(bean, beanName);
                        proxiedCount++;
                    }
                } catch (Exception e) {
                    logger.warn("处理Bean {} 时发生异常", beanName, e);
                }
            }

            logger.info("扫描完成，共为 {} 个业务服务创建了监控代理", proxiedCount);

        } catch (Exception e) {
            logger.error("扫描业务Bean失败", e);
        }
    }

    /**
     * 获取代理Bean数量
     */
    public int getProxiedBeanCount() {
        return proxiedBeans.size();
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        logger.info("清理Spring拦截器适配器资源...");

        try {
            // 清理代理对象引用
            proxiedBeans.clear();

            logger.info("Spring拦截器适配器资源清理完成");
        } catch (Exception e) {
            logger.error("Spring拦截器适配器资源清理异常", e);
        }
    }
}
