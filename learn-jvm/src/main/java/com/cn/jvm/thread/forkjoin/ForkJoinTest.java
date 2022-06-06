package com.cn.jvm.thread.forkjoin;

import java.util.concurrent.ForkJoinPool;

/**
 * @Description: ForkJoinTest
 * @Author: 一方通行
 * @Date: 2022-04-16
 * @Version:v1.0
 */
public class ForkJoinTest {
    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.execute(() -> System.out.println("123"));
    }
}
