package com.cn.jvm.jmh;

import org.openjdk.jmh.annotations.*;

/**
 * {@code @Description: JmhTest}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2024-11-16}
 * {@code @Version:v1.0}
 */
public class JmhTest {

    @Benchmark
    @Warmup(iterations = 1, time = 3)//在专业测试里面首先要进行预热，预热多少次，预热多少时间
    @Fork(100)//意思是用多少个线程去执行我们的程序
    @BenchmarkMode(Mode.Throughput)//是对基准测试的一个模式，这个模式用的最多的是Throughput吞吐量
    @Measurement(iterations = 1, time = 3)//是整个测试要测试多少遍，调用这个方法要调用多少次
    public void testForEach() {
        JMHMain.foreach();
    }

}
