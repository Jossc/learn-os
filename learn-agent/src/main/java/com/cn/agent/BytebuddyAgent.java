package com.cn.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

/**
 * @Description: BytebuddyAgent
 * @Author: 一方通行
 * @Date: 2021-08-14
 * @Version:v1.0
 */
public class BytebuddyAgent {

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        Class<?> bootStrapClass = Class.forName("", true, getClassLoader());
        //Class<?> bootStrapClass = Class.forName("com.tinysakura.bytebuddylearn.BootStrap");
        Method preMain = bootStrapClass.getMethod("preMain", String.class, Instrumentation.class);
        preMain.invoke(null, agentArgs, inst);
    }

    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            return classLoader;
        }
        return ClassLoader.getSystemClassLoader();
    }
}
