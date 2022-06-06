package com.cn.agent;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @Description: TimeInterceptor
 * @Author: 一方通行
 * @Date: 2021-08-08
 * @Version:v1.0
 */
public class TimeInterceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<?> callable) throws Exception {
        long start = System.currentTimeMillis();
        try {
            return callable.call(); // 执行原函数
        } finally {
            System.out.println(method.getName() + ":"
                    + (System.currentTimeMillis() - start) + "ms");
        }
    }
}
