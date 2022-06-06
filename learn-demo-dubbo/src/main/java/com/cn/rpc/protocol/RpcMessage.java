package com.cn.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: RpcMessage
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
@Data
public class RpcMessage<T> implements Serializable {

    private static final long serialVersionUID = -6138514840214281279L;

    private RpcHeader header;

    private T content;

    public RpcMessage() {

    }

    public RpcMessage(RpcHeader header, T content) {
        this.header = header;
        this.content = content;
    }
}
