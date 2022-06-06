package com.cn.easy;

import java.util.Arrays;

/**
 * @Description: Number436
 * @Author: 一方通行
 * @Date: 2022-05-19
 * @Version:v1.0
 */
public class Number462 {

    public int minMoves2(int[] nums) {
        Arrays.sort(nums);
        int n = nums.length, ret = 0, x = nums[n / 2];
        for (int i = 0; i < n; i++) {
            ret += Math.abs(nums[i] - x);
        }
        return ret;
    }
}
