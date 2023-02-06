package com.cn.jvm.jvm;

import java.util.concurrent.locks.LockSupport;

/**
 * @Description: Demo
 * @Author: 一方通行
 * @Date: 2021-11-14
 * @Version:v1.0
 */
public class Demo {
    public static void main(String[] args) throws Exception {
        /*LockSupport.park();

*/
        int val = (int) 4294967297L;
        System.out.println( val);
        int val2 = (int)4294967294L;
        System.out.println( val2);
    }
}
