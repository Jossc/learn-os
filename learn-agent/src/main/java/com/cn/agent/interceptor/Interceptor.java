package com.cn.agent.interceptor;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

/**
 * @Description: Interceptor
 * @Author: 一方通行
 * @Date: 2021-08-14
 * @Version:v1.0
 */
public class Interceptor {

    @Advice.OnMethodEnter
    //@Advice.This注解我们可以拿到实际执行方法的对象，通过@Advice.Origin注解可以拿到原方法，
    public static void enter(@Advice.This Thread thread, @Advice.Origin Method origin) {
        System.out.println("thread:" + thread.getName() + " enter thread timeMills:" + System.currentTimeMillis());
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();


        if (classLoader == null) {
            try {
                origin.invoke(null, thread);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            Class<?> reflectClass = Class.forName("com.cn.agent.client.test.CustomThread", true, classLoader);
            Method start = reflectClass.getMethod("start", Thread.class);
            start.invoke(null, thread);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.This Thread thread, @Advice.Origin Method origin) {
        System.out.println("thread:" + thread.getName() + " exit thread timeMills:" + System.currentTimeMillis());
    }
}
