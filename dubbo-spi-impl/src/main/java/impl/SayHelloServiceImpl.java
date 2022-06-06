package impl;

import api.SayHelloService;

/**
 * @Description: SayHelloServiceImpl
 * @Author: 一方通行
 * @Date: 2021-08-01
 * @Version:v1.0
 */
public class SayHelloServiceImpl implements SayHelloService {
    @Override
    public String sayHello() {
        return "这是 dubbo spi";
    }
}
