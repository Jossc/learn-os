package com.cn.learn.impl;

import com.cn.learn.api.HelloService;

/**
 * @Description: HelloServiceImpl
 * @Author: 一方通行
 * @Date: 2021-08-01
 * @Version:v1.0
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String hello) {
        return hello + "这个可以真的牛逼";
    }
}
