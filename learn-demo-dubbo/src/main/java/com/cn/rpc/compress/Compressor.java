package com.cn.rpc.compress;

import java.io.IOException;

/**
 * @Description: Compressor
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public interface Compressor {

    byte[] compress(byte[] array) throws IOException;

    byte[] unCompress(byte[] array) throws IOException;
}
