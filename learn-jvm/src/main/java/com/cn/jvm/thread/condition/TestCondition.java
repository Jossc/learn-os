package com.cn.jvm.thread.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author: 一方通行
 */
public class TestCondition {

    public static class Number {
        public int value = 1;
    }

    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();

        Condition oneCondition = reentrantLock.newCondition();
        Condition twoCondition = reentrantLock.newCondition();

        final Number number = new Number();

        Thread threadA = new Thread(() -> {
            //1 -3
            try {
                reentrantLock.lock();
                System.out.println("ThreadA is running");
                while (number.value <= 3) {
                    System.out.println("Number: " + number.value);
                    number.value++;

                }
                oneCondition.signal();
            } catch (Exception exception) {

            } finally {
                reentrantLock.unlock();
            }
            reentrantLock.lock();
            try {
                twoCondition.await();
                System.out.println("ThreadA is running");
                while (number.value <= 9) {
                    System.out.println("Number: " + number.value);
                    number.value++;

                }
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }

        });

        Thread threadB = new Thread(() -> {
            try {
                reentrantLock.lock();
                // 1-3 需要阻塞
                while (number.value <= 3) {
                    oneCondition.await();
                }
            } catch (Exception exception) {

            } finally {
                reentrantLock.unlock();
            }

            try {
                reentrantLock.lock();
                System.out.println("ThreadB is running");
                while (number.value <= 6) {
                    System.out.println("Number: " + number.value);
                    number.value++;

                }
                twoCondition.signal();
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        });

        threadA.start();
        threadB.start();
    }
}
