package com.cn.agent;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description: AgentClassLoader
 * @Author: 一方通行
 * @Date: 2021-08-08
 * @Version:v1.0
 */
public class AgentClassLoader extends ClassLoader {

    static {
        registerAsParallelCapable();
    }

    private List<File> classpath;

    private static AgentClassLoader DEFAULT_LOADER;


    public static AgentClassLoader getDefault() {
        return DEFAULT_LOADER;
    }

    public AgentClassLoader(ClassLoader parent) {
        super(parent);
        classpath = new LinkedList<>();
    }

   /* @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }*/
}
