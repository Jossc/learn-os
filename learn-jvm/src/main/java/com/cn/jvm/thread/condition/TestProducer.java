package com.cn.jvm.thread.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author: 一方通行
 */
public class TestProducer {

    public Lock lock = new ReentrantLock();

    public boolean flag = false;

    public int number = 0;

    public Condition producerCondition = lock.newCondition();

    public Condition consumerCondition = lock.newCondition();

    public static void main(String[] args) {

        Thread a = new Thread(new ProducerRunnable());
        Thread b = new Thread(new ConditionRunnable());
        a.start();
        b.start();

    }

    public void setProducerCondition() {
        System.out.println("producerCondition running");
        try {
            lock.lock();
            while (flag) {
                number++;
                System.out.println("setProducerCondition " + number);
                producerCondition.wait();
                consumerCondition.signal();
            }
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
    }

    public void setConsumerCondition() {
        System.out.println("setConsumerCondition running");
        try {
            lock.lock();
            while (!flag) {
                number--;
                System.out.println("setConsumerCondition " + number);
                consumerCondition.wait();
                producerCondition.signal();
            }
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
    }
}

