package com.cn.easy;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description: Number217
 * @Author: 一方通行
 * @Date: 2022-05-28
 * @Version:v1.0
 */
public class Number217 {

    public boolean containsDuplicate(int[] nums) {
        if (nums.length == 0) {
            return false;
        }
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            if (!set.add(num)) {
                return true;
            }
        }

        return false;
    }
}
