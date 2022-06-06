package com.cn.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.Permission;
import java.security.ProtectionDomain;
import java.util.Enumeration;

/**
 * @Description: DefineTransformer
 * @Author: 一方通行
 * @Date: 2021-08-07
 * @Version:v1.0
 */
public class DefineTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("premain load Class:" + className);
        System.out.println("premain loader:" + loader.toString());
        System.out.println("我草 这里到底能拿到什么玩意");
        System.out.println(new String(classfileBuffer));
        Module module = classBeingRedefined.getModule();
        module.getClassLoader();
        Enumeration<Permission> elements = protectionDomain.getPermissions().elements();
        while (elements.hasMoreElements()) {
            Permission permission = elements.nextElement();
            System.out.println("permission " + " : " + permission.getName());
        }
        return classfileBuffer;
    }
}
