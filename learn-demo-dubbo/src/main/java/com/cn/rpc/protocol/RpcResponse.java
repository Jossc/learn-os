package com.cn.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: RpcResponse
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
@Data
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -4621798084809615235L;

    /**
     * 响应的错误码，正常响应为0，非0表示异常响应
     */
    private int code = 0;

    /**
     * 异常信息
     */
    private String msg;

    /**
     * 响应结果
     */
    private Object result;
}
