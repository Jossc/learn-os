package com.cn.rpc.test;

/**
 * @Description: DemoServiceImpl
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class DemoServiceImpl implements DemoService {
    public String sayHello(String param) {
        System.out.println("param" + param);
        return "hello:" + param;
    }
}
