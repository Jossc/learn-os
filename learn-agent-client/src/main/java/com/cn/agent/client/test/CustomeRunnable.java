package com.cn.agent.client.test;

/**
 * @Description: CustomeRunnable
 * @Author: 一方通行
 * @Date: 2021-08-14
 * @Version:v1.0
 */
public class CustomeRunnable implements Runnable {
    Runnable delegate;

    public CustomeRunnable(Runnable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void run() {
        System.out.println("welcome to my custom thread");
        delegate.run();
    }
}
