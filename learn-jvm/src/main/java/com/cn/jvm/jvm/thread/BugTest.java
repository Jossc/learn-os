package com.cn.jvm.jvm.thread;

/**
 * @Description: BugTest
 * @Author: 一方通行
 * @Date: 2022-11-06
 * @Version:v1.0
 */
public class BugTest {
    static long counter;

    public static void main(String[] args) throws Exception {
        System.out.println("main start");
        startBusinessThread();
        startProblemThread();
        // 等待线程启动执行
        Thread.sleep(1000);
        // 执行GC
        System.gc();
        System.out.println("main end");
    }

    public static void startProblemThread() {
        new Thread(new MyRun()).start();
    }

    public static class MyRun implements Runnable {

        @Override
        public void run() {
            System.out.println("Problem start");
            for (int i = 0; i < 100000000; i++) {
                for (int j = 0; j < 1000; j++) {
                    counter += i % 33;
                    counter += i % 333;
                }
            }
            System.out.println("Problem end");
        }
    }

    public static void startBusinessThread() {
        new Thread(() -> {
            System.out.println("业务线程-1 start");
            for (; ; ) {
                System.out.println("执行业务1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            System.out.println("业务线程-2 start");
            for (; ; ) {
                System.out.println("执行业务2");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
