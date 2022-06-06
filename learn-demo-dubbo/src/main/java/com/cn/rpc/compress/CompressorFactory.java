package com.cn.rpc.compress;

/**
 * @Description: CompressorFactory
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class CompressorFactory {
    public static Compressor get(byte extraInfo) {
        switch (extraInfo & 24) {
            case 0x0:
                return new DefCompressor();
            default:
                return new DefCompressor();
        }
    }
}
