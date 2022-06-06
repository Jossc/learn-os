package com.cn.easy;

/**
 * @Description: Number53
 * @Author: 一方通行
 * @Date: 2022-05-28
 * @Version:v1.0
 */
public class Number53 {

    public int maxSubArray(int[] nums) {
        int pre = 0, max = nums[0];
        for (int num : nums) {
            pre = Math.max(pre + num, num);
            max = Math.max(max, pre);
        }
        return max;
    }
}
