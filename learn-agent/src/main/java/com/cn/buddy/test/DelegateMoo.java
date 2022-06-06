package com.cn.buddy.test;

/**
 * @Description: DelegateMoo
 * @Author: 一方通行
 * @Date: 2021-08-14
 * @Version:v1.0
 */
public class DelegateMoo {
    public static String Moo(String param1, Integer param2) {
        return "my name is " + param1 + ",my age is " + param2;
    }

    public static String Moo1(String param1) {
        return "my name is " + param1;
    }
}
