package com.cn.buddy.test;

import net.bytebuddy.implementation.bind.annotation.FieldValue;

/**
 * @Description: DelegateWithField
 * @Author: 一方通行
 * @Date: 2021-08-14
 * @Version:v1.0
 */
public class DelegateWithField {
    String name;

    public static String Moo1(String param1, @FieldValue(value = "name") String name) throws Exception {
        return "parm1:" + param1 +
                ";name:" + name;
    }
}
