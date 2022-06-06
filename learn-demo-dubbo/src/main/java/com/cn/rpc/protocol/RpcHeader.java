package com.cn.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: RpcHeader
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
@Data
public class RpcHeader implements Serializable {

    private static final long serialVersionUID = -1441958069658067646L;

    /**
     * 魔数
     */
    private short magic;

    /**
     * 版本号
     */
    private byte version;

    /**
     * 附加信息
     * 0消息类型
     * 1-2 序列方式
     * 3-4 压缩方式
     * 5-6 请求类型
     */
    private byte extraInfo;

    /**
     * 消息id
     */
    private Long messageId;

    /**
     * 消息长度
     */
    private Integer size;

    public RpcHeader(short magic, byte version) {
        this.magic = magic;
        this.version = version;
        this.extraInfo = 0;
    }

    public RpcHeader(short magic, byte version, byte extraInfo, Long messageId, Integer size) {
        this.magic = magic;
        this.version = version;
        this.extraInfo = extraInfo;
        this.messageId = messageId;
        this.size = size;
    }
}
