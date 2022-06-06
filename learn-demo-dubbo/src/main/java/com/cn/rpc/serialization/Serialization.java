package com.cn.rpc.serialization;

import java.io.IOException;

/**
 * @Description: Serialization
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public interface Serialization {
    /**
     * 序列化对象
     *
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     *
     * @param data
     * @param clz
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}
