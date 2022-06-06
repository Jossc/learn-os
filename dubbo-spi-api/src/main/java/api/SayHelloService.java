package api;

import org.apache.dubbo.common.extension.SPI;

/**
 * @Description: SayHelloService
 * @Author: 一方通行
 * @Date: 2021-08-01
 * @Version:v1.0
 */
@SPI
public interface SayHelloService {

    String sayHello();
}
