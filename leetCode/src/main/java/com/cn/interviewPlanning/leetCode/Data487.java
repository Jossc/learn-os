package com.cn.interviewPlanning.leetCode;

/**
 * @Description: Data487
 * @Author: 一方通行
 * @Date: 2023-12-12
 * @Version:v1.0
 */
public class Data487 {

    public int findMaxConsecutiveOnes(int[] nums) {
        int ans = 0, l = 0, zero = -1;
        int r = nums.length;
        for (int i = 0; i < r; i++) {
            if (nums[i] == 0) {
                l = zero + 1;
                zero = r;
            }
            ans = Math.max(ans, r - l + 1);
        }
        return ans;
    }


}
