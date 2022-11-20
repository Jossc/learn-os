package com.cn.jvm.jvm.fina;

/**
 * author: 一方通行
 */
public class TestFinal {

    public static void main(String[] args) {
        System.out.println("hello world!");

    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalize finished");
        super.finalize();
    }
}
