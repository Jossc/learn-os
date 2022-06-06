package com.cn.easy;

import java.util.Arrays;
import java.util.Collections;

/**
 * @Description: Number268
 * @Author: 一方通行
 * @Date: 2021-11-06
 * @Version:v1.0
 */
public class Number268 {


    public int missingNumber(int[] nums) {
        Arrays.sort(nums);
        int max = nums[nums.length - 1];
        int min = nums[0];
        int length = nums.length;
        if (length > max) {
            return length;
        }
        for (int i = 0; i < max; ++i) {
            if (min == nums[i]) {
                ++min;
                continue;
            } else {
                return min;
            }
        }
        return 0;
    }
}
