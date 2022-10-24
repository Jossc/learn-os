package com.cn.easy;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;


/**
 * @Description: Number870
 * @Author: 一方通行
 * @Date: 2022-10-08
 * @Version:v1.0
 */
public class Number870 {

    public int[] advantageCount(int[] nums1, int[] nums2) {
        int len = nums1.length;
        TreeSet<Integer> result = new TreeSet<Integer>();
        Map<Integer, Integer> map = new HashMap<>();
        for (int x : nums1) {
            map.put(x, map.getOrDefault(x, 0) + 1);
            if (map.get(x) == 1) {
                result.add(x);
            }
        }
        int[] ans = new int[len];
        for (int i = 0; i < len; i++) {
            Integer cur = result.ceiling(nums2[i] + 1);
            if (cur == null) cur = result.ceiling(-1);
            ans[i] = cur;
            map.put(cur, map.get(cur) - 1);
            if (map.get(cur) == 0) result.remove(cur);
        }
        return ans;
    }
}
