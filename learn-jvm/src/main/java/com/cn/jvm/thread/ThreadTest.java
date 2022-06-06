package com.cn.jvm.thread;

import java.util.stream.IntStream;

/**
 * @Description: ThreadTest
 * @Author: 一方通行
 * @Date: 2022-01-03
 * @Version:v1.0
 */
public class ThreadTest {
    int value = 0;

    public static void main(String[] args) throws InterruptedException {
        final int count = 10000;
        final ThreadTest thread = new ThreadTest();
        Thread t1 = new Thread(() -> IntStream.range(0, count).forEach((i) -> thread.add()));
        Thread t2 = new Thread(() -> IntStream.range(0, count).forEach((i) -> thread.add()));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    void add() {
        value++;
    }
}
