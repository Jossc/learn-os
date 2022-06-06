package com.cn.easy;

import java.util.concurrent.Semaphore;

/**
 * @Description: Thread
 * @Author: 一方通行
 * @Date: 2021-12-18
 * @Version:v1.0
 */
public class TestThread {
    private static Semaphore A = new Semaphore(1);
    private static Semaphore B = new Semaphore(1);
    private static Semaphore C = new Semaphore(1);


    static class TestThreadA extends Thread {
        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    A.acquire();
                    System.out.println("甲");
                    B.release();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    static class TestThreadB extends Thread {
        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    B.acquire();
                    System.out.println("甲");
                    C.release();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    static class TestThreadC extends Thread {
        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    C.acquire();
                    System.out.println("甲");
                    A.release();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }



}
