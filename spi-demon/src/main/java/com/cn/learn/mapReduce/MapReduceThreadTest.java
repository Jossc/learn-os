package com.cn.learn.mapReduce;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @Description: MapReduceThreadTest
 * @Author: 一方通行
 * @Date: 2023-12-29
 * @Version:v1.0
 */
public class MapReduceThreadTest {
    private static final int NUM_THREADS = 4;

    public static void main(String[] args) {
        // 读取输入数据
        String[] inputArray = {"apple orange banana", "banana orange", "apple banana"};
        AtomicInteger currentPosition = new AtomicInteger(0);
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        Map<String, Integer> intermediateResult = new HashMap<>();

        // 创建Map任务
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                Map<String, Integer> threadResult = new HashMap<>();
                int position;
                while ((position = currentPosition.getAndIncrement()) < inputArray.length) {
                    String line = inputArray[position];
                    String[] words = line.split(" ");
                    for (String word : words) {
                        threadResult.put(word, threadResult.getOrDefault(word, 0) + 1);
                    }
                }

                synchronized (intermediateResult) {
                    // 合并各线程的中间结果
                    for (Map.Entry<String, Integer> entry : threadResult.entrySet()) {
                        String key = entry.getKey();
                        int count = entry.getValue();
                        intermediateResult.put(key, intermediateResult.getOrDefault(key, 0) + count);
                    }
                }
            });
        }

        // 等待任务完成
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // 等待所有任务执行完毕
        }
        // 输出最终结果
        for (Map.Entry<String, Integer> entry : intermediateResult.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

}
