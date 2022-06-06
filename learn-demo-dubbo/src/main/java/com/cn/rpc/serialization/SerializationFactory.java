package com.cn.rpc.serialization;

/**
 * @Description: Compressor
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class SerializationFactory {
    public static Serialization get(byte type) {
        switch (type & 0x7) {

            case 0x0:
                return new HessianSerialization();
            default:
                return new HessianSerialization();
        }

    }
}
