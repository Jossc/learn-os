package com.cn.jvm.asm;

/**
 * @Description: MyClassLoader
 * @Author: 一方通行
 * @Date: 2024-02-11
 * @Version:v1.0
 */
public class MyClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if ("com.cn.jvm.asm.HelloWorld".equals(name)) {
            byte[] bytes = HelloWorldDump.dumpHello();
            Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
            return clazz;
        }

        throw new ClassNotFoundException("Class Not Found: " + name);
    }
}
