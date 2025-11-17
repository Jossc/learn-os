package com.monitor.plugin.plugin;

import com.monitor.plugin.config.SpringBeanInterceptorAdvisor;
import com.monitor.plugin.metric.BusinessMetricRegistry;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.DefaultImplementor;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;

/**
 * 业务插件启动服务
 * 负责插件的初始化、启动和关闭
 *
 * @Author: 架构师
 * @Date: 2025-10-20
 */
@DefaultImplementor
public class BusinessBootService implements BootService {

    private static final ILog logger = LogManager.getLogger(BusinessBootService.class);

    private BusinessMetricRegistry metricRegistry;
    private SpringBeanInterceptorAdvisor springAdvisor;
    private volatile boolean initialized = false;

    @Override
    public void prepare() throws Throwable {
        logger.info("准备业务指标插件...");

        try {
            // 初始化指标注册器
            this.metricRegistry = new BusinessMetricRegistry();

            // 初始化Spring适配器
            this.springAdvisor = new SpringBeanInterceptorAdvisor(metricRegistry);

            logger.info("业务指标插件准备完成");
        } catch (Exception e) {
            logger.error("业务指标插件准备失败", e);
            throw e;
        }
    }

    @Override
    public void boot() throws Throwable {
        logger.info("启动业务指标插件...");

        try {
            // 初始化业务指标定义
            metricRegistry.initializeBusinessMetrics();

            // 配置Spring集成
            initializeSpringIntegration();

            this.initialized = true;
            logger.info("业务指标插件启动完成");

        } catch (Exception e) {
            logger.error("业务指标插件启动失败", e);
            throw e;
        }
    }

    @Override
    public void onComplete() throws Throwable {
        if (initialized) {
            logger.info("业务指标插件初始化完成，开始监控业务指标");

            // 注册Spring Bean后处理器
            springAdvisor.registerBeanPostProcessors();

            // 启动指标收集
            metricRegistry.startMetricCollection();
        }
    }

    @Override
    public void shutdown() throws Throwable {
        logger.info("关闭业务指标插件...");

        try {
            if (springAdvisor != null) {
                springAdvisor.cleanup();
            }

            if (metricRegistry != null) {
                metricRegistry.shutdown();
            }

            this.initialized = false;
            logger.info("业务指标插件已关闭");

        } catch (Exception e) {
            logger.error("业务指标插件关闭异常", e);
        }
    }

    @Override
    public int priority() {
        return 1000; // 较高优先级，在基础服务之后启动
    }

    /**
     * 初始化Spring集成
     */
    private void initializeSpringIntegration() {
        try {
            // 检测Spring环境
            if (isSpringEnvironment()) {
                logger.info("检测到Spring环境，启用Spring AOP集成");
                springAdvisor.configureSpringAOP();
            } else {
                logger.info("未检测到Spring环境，使用标准拦截器模式");
            }
        } catch (Exception e) {
            logger.warn("Spring集成初始化失败，降级到标准模式", e);
        }
    }

    /**
     * 检测是否为Spring环境
     */
    private boolean isSpringEnvironment() {
        try {
            Class.forName("org.springframework.context.ApplicationContext");
            Class.forName("org.springframework.aop.framework.ProxyFactory");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取指标注册器实例
     */
    public static BusinessMetricRegistry getMetricRegistry() {
        // 从ServiceManager获取实例
        BusinessBootService bootService = org.apache.skywalking.apm.agent.core.boot.ServiceManager.INSTANCE.findService(BusinessBootService.class);
        return bootService != null ? bootService.metricRegistry : null;
    }
}
