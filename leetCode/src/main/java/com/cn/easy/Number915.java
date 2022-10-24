package com.cn.easy;

/**
 * @Description: Number915
 * @Author: 一方通行
 * @Date: 2022-10-24
 * @Version:v1.0
 */
public class Number915 {

    public int partitionDisjoint(int[] nums) {
        int currSum = nums[0];
        int leftMax = nums[0], leftPos = 0;
        for (int k = 1; k < nums.length - 1; k++) {
            currSum = Math.max(currSum, nums[k]);
            if (nums[k] < leftMax) {
                leftMax = currSum;
                leftPos = k;
            }
        }
        return leftPos + 1;
    }
}
