package com.cn.easy;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @Description: Number347
 * @Author: 一方通行
 * @Date: 2022-04-21
 * @Version:v1.0
 */
public class Number347 {

    public static void main(String[] args) {
        int[] i = {1, 1, 1, 2, 2, 3};
        new Number347().topKFrequent(i, 2);
    }

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        PriorityQueue<int[]> queue = new PriorityQueue<int[]>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int num = entry.getKey();
            int count = map.get(num);
            //判断 queue 是不是和前top一样
            if (queue.size() == k) {
                if (queue.peek()[1] < count) {
                    queue.poll();
                    queue.offer(new int[]{num, count});
                }
            } else {
                queue.offer(new int[]{num, count});
            }
        }
        //这里取top的数值
        int[] ret = new int[k];
        for (int i = 0; i < k; ++i) {
            ret[i] = queue.poll()[0];
        }
        return ret;
    }
}
