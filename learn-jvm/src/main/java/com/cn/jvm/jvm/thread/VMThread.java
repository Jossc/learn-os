package com.cn.jvm.jvm.thread;

/**
 * @Description: VMThread
 * @Author: 一方通行
 * @Date: 2022-10-24
 * @Version:v1.0
 */
public class VMThread {
    public static void main(String[] args) throws InterruptedException {
        ThreadTest thread1 = new ThreadTest("thread1");
        ThreadTest thread2 = new ThreadTest("thread2");
        thread1.sleep(0L);
        thread1.yield();
        thread1.start();
        thread2.start();
        System.out.println("thread1 111  sleep ");

    }

    public static class ThreadTest extends Thread {
        public String name;

        public ThreadTest(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(name);
        }
    }


}
