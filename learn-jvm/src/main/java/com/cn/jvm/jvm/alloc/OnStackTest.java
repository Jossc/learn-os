package com.cn.jvm.jvm.alloc;

/**
 * @Description: OnStackTest
 * @Author: 一方通行
 * @Date: 2021-11-22
 * @Version:v1.0
 */
public class OnStackTest {

    public static class User {

        public int id = 0;

        public String name = "";
    }

    public static void alloc() {
        User user = new User();
        user.id = 10;
        user.name = "1";
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            alloc();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
