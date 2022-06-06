package com.cn.rpc.registry;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: ServerInfo
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
@Data
public class ServerInfo implements Serializable {


    private static final long serialVersionUID = -3570055349237365928L;
    private String host;

    private int port;

    public ServerInfo() {
    }

    public ServerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
