package com.cn.rpc.constants;

/**
 * @Description: Constants
 * @Author: 一方通行
 * @Date: 2021-08-15
 * @Version:v1.0
 */
public class Constants {
    public static final short MAGIC = (short) 0xE0F1;

    public static final int HEADER_SIZE = 16;

    public static final byte VERSION_1 = 1;

    public static final long DEFAULT_TIMEOUT = 500000;

    public static final int CONNECTION_TIMEOUT = 2000;

    public static final int DEF_PORT = 9999;

    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    public final static int HEARTBEAT_CODE = -1;

    public final static byte HEART_EXTRA_INFO = 1;

    public static boolean isHeartBeat(byte extraInfo) {
        return (extraInfo & 32) != 0;
    }

    public static boolean isRequest(byte extraInfo) {
        return (extraInfo & 1) != 1;
    }
}
