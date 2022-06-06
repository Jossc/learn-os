package com.cn.agent.client;

import java.util.concurrent.TimeUnit;

/**
 * @Description: Main
 * @Author: 一方通行
 * @Date: 2021-08-07
 * @Version:v1.0
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
       /* Thread.sleep(1000);
        System.out.println("agent test");
        Main main = new Main();
        main.testAgent("不知道,","这能打出来什么");*/
        new Thread(() -> {
            System.out.println("hello bytebuddy agent");
            for (int i = 0; i < 10000L; i++) {

            }
        }, "hello").start();
    }

    public void testAgent(String agentArgs, String value) {
        System.out.println("testAgent  agentArgs + :" + agentArgs);
        System.out.println("testAgent  value + :" + value);

    }
}
