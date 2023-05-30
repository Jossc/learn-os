package com.cn.jvm.cleaner;

/**
 * author: 一方通行
 */
public class DoSomethingThread implements Runnable {
    private String name;

    public DoSomethingThread(String name) {
        this.name = name;
    }

    // do something before gc
    @Override
    public void run() {
        System.out.println(name + " running DoSomething ...");
    }
}
