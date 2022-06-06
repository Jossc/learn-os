package com.cn.buddy.test;

import net.bytebuddy.implementation.bind.annotation.Super;

/**
 * @Description: DelegateMooWithSuper
 * @Author: 一方通行
 * @Date: 2021-08-14
 * @Version:v1.0
 */
public class DelegateMooWithSuper {

    public static String Moo1(String param1, @Super Moo zuper) {
        System.out.println("invoke time:" + System.currentTimeMillis());
        return zuper.Moo1(param1);
    }
}
