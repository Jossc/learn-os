package com.cn.easy;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Number532
 * @Author: 一方通行
 * @Date: 2022-06-16
 * @Version:v1.0
 */
public class Number532 {
    public int findPairs(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        int ans = 0;
        for (int num : nums) {
            if (map.get(num) == 0) {
                continue;
            }
            if (k == 0) {
                if (map.get(num) > 1)
                    ans++;
            } else {
                int a = num - k, b = num + k;
                if (map.getOrDefault(a, 0) > 0)
                    ans++;
                if (map.getOrDefault(b, 0) > 0)
                    ans++;
            }
            map.put(num, 0);
        }
        return ans;
    }
}
