package com.cn.rpc.compress;

import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * @Description: DefCompressor
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class DefCompressor  implements Compressor {

    @Override
    public byte[] compress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }
        return Snappy.compress(array);
    }

    @Override
    public byte[] unCompress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }
        return Snappy.uncompress(array);
    }
}
