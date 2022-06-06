package com.cn.easy;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description: Number961
 * @Author: 一方通行
 * @Date: 2022-05-21
 * @Version:v1.0
 */
public class Number961 {

    public int repeatedNTimes(int[] nums) {
        Set<Integer> found = new HashSet<Integer>();
        for (int num : nums) {
            if (!found.add(num)) {
                return num;
            }
        }
        return -1;
    }
}
