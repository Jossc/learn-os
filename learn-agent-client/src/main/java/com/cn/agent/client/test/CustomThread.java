package com.cn.agent.client.test;

import java.lang.reflect.Field;

/**
 * @Description: CustomThread
 * @Author: 一方通行
 * @Date: 2021-08-14
 * @Version:v1.0
 */
public class CustomThread {
    public static void start(Thread thread) {
        try {
            System.out.println("welcome to my custom thread");
            Field field = Thread.class.getDeclaredField("target");
            field.setAccessible(true);
            Object targetValue = field.get(thread);
            if (targetValue instanceof Runnable) {
                Runnable runnable = (Runnable) targetValue;
                field.set(thread, new CustomeRunnable(runnable));
            }
        } catch (Throwable e) {
            System.out.println(e);
            ;
        }
    }

    public static void main(String[] args) throws InterruptedException {
       /* Thread.sleep(1000);
        System.out.println("agent test");
        Main main = new Main();
        main.testAgent("不知道,","这能打出来什么");*/
        start(new Thread(() -> {
            System.out.println("hello bytebuddy agent");
            for (int i = 0; i < 10000L; i++) {

            }
        }, "hello"));
    }
}
