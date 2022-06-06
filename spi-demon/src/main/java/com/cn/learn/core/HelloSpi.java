package com.cn.learn.core;

import com.cn.learn.api.HelloService;

import java.util.ServiceLoader;

/**
 * @Description: HelloSpi
 * @Author: 一方通行
 * @Date: 2021-08-01
 * @Version:v1.0
 */
public class HelloSpi {


    public static void main(String[] args) {
        ServiceLoader<HelloService> helloServices = ServiceLoader.load(HelloService.class);
        helloServices.stream();
       /* for (HelloService service : helloServices){
            System.out.println(service.sayHello("ahah"));
        }*/
    }
}
