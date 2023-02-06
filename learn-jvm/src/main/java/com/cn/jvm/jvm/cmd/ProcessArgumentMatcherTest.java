package com.cn.jvm.jvm.cmd;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @Description: ProcessArgumentMatcherTest
 * @Author: 一方通行
 * @Date: 2022-11-27
 * @Version:v1.0
 */
public class ProcessArgumentMatcherTest {
    public static void main(String[] args) throws Exception {
        final Field theUnsafe;
        try {
            theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe  = (Unsafe) theUnsafe.get(null);
            System.out.println(unsafe.getClass().getClassLoader());
            System.out.println(unsafe);
//            unsafe.allocateMemory()
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
