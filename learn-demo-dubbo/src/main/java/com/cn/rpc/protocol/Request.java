package com.cn.rpc.protocol;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @Description: Request
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
@Data
public class Request implements Serializable {

    private static final long serialVersionUID = 7834694294865086724L;

    /**
     * service名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 请求方法参数类型
     */
    private Class[] argTypes;

    /**
     * 方法参数
     */
    private Object[] args;

    public Request() {
    }

    public Request(String serviceName, String methodName, Object[] args) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.args = args;
        this.argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
