package com.cn.jvm.jvm.classt;

import java.lang.reflect.Field;

/**
 * @Description: ClassTest
 * @Author: 一方通行
 * @Date: 2022-10-25
 * @Version:v1.0
 */
public class ClassTest {

    public static void main(String[] args) throws ClassNotFoundException {
        final Field[] classTests = Class.forName("com.cn.jvm.jvm.classt.ClassTest").getDeclaredFields();
    }
}
