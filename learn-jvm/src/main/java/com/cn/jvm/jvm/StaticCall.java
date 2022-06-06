package com.cn.jvm.jvm;

import java.util.concurrent.TimeUnit;

/**
 * @Description: StaticCall
 * @Author: 一方通行
 * @Date: 2021-08-06
 * 测试静态变量寻址
 * @Version:v1.0
 */
public class StaticCall {

    public static final int TabNine = 1;

    public static void main(String[] args) throws InterruptedException {
        new StaticCall().test();

        Thread thread = new Thread();

        TimeUnit.SECONDS.sleep(1);
    }

    void test() {
        System.out.println(this.TabNine);
        System.out.println(TabNine);
    }
}

