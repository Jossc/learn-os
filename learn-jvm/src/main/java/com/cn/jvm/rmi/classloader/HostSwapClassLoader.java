package com.cn.jvm.rmi.classloader;

/**
 * @Description: HostSwapClassLoader
 * @Author: 一方通行
 * @Date: 2021-11-20
 * @Version:v1.0
 */
public class HostSwapClassLoader extends ClassLoader {

    public HostSwapClassLoader() {
        super(HostSwapClassLoader.class.getClassLoader());
    }

    public Class loadByte(byte[] classBytes) {
        return defineClass(null, classBytes, 0, classBytes.length);
    }
}
