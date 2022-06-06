package com.cn.jvm.jvm.alloc;

/**
 * @Description: NewSizeDemo
 * @Author: 一方通行
 * @Date: 2021-11-22
 * @Version:v1.0
 */
public class NewSizeDemo {

    public static void main(String[] args) {
        byte[] b = null;
        for (int i = 0; i < 10; i++) {
            b = new byte[1024 * 1024];
        }
    }
}

