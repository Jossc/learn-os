package com.cn.jvm.asm;

/**
 * @Description: HelloWorldRun
 * @Author: 一方通行
 * @Date: 2024-02-11
 * @Version:v1.0
 */
public class HelloWorldRun {
    public static void main(String[] args) throws Exception {
        MyClassLoader classLoader = new MyClassLoader();
        Class<?> clazz = classLoader.loadClass("com.cn.jvm.asm.HelloWorld");
        Object instance = clazz.newInstance();
        System.out.println(instance);
    }
}
