package com.cn.jvm.method;

import sun.reflect.ReflectionFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * @Description: MethodHandleTest
 * @Author: 一方通行
 * @Date: 2021-12-05
 * @Version:v1.0
 */
public class MethodHandleTest {
    public static void main(String[] args) throws Throwable {
    /*    MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Class<?> aClass = lookup.findClass("com.cn.jvm.jvm.Demo");
        final String name = aClass.getName();
        System.out.println("name " + name);


        Constructor constructor = String.class.getConstructor(byte[].class);*/
        MethodHandleTest methodHandleTest = new MethodHandleTest();
        /*   methodHandleTest.testArray();*/
        final Method testArray = methodHandleTest.getClass().getMethod("testSay", null);
        testArray.invoke(methodHandleTest);

        /*   methodHandleTest.getClass().getDeclaredMethod();*/


        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

    }

    public void testSay() throws RuntimeException {
        System.out.println("say hello");
    }

    public void testArray() throws Throwable {
        int[] array = new int[]{1, 2, 3, 4, 5};
        MethodHandle arrayElementSetter = MethodHandles.arrayElementSetter(int[].class);
        arrayElementSetter.invoke(array, 3, 8);
        MethodHandle arrayElementGetter = MethodHandles.arrayElementGetter(int[].class);
        int value = (int) arrayElementGetter.invoke(array, 3);
        System.out.println(value);
    }
}
