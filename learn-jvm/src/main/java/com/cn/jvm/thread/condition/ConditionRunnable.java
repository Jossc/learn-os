package com.cn.jvm.thread.condition;

/**
 * author: 一方通行
 */
public class ConditionRunnable implements Runnable {
    public TestProducer testProducer = new TestProducer();

    @Override
    public void run() {
        testProducer.setConsumerCondition();
    }
}
