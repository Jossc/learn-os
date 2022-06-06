package com.cn.easy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @Description: Number824
 * @Author: 一方通行
 * @Date: 2022-04-21
 * @Version:v1.0
 */
public class Number824 {

    // list 维护元音字母
    public static final List list = Arrays.asList('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');


    public String toGoatLatin(String sentence) {
        String[] latin = sentence.split(" ");
        int len = latin.length;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            if (list.contains(latin[i].charAt(0))) {
                sb.append(latin[i]);
            } else {
                sb.append(latin[i].substring(1));
                sb.append(latin[i].charAt(0));
            }
            sb.append("ma");
            for (int j = 0; j < i + 1; j++) {
                sb.append('a');
            }
            if (i != len - 1) sb.append(' ');
        }

        return sb.toString();
    }


    public static void main(String[] args) {
        int[] i = {1, 1, 1, 2, 2, 3};
        new Number824().topKFrequent(i, 2);
    }

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        PriorityQueue<int[]> queue = new PriorityQueue<int[]>(new Comparator<int[]>() {
            public int compare(int[] m, int[] n) {
                return m[1] - n[1];
            }
        });

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
