package com.cn.easy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Number396
 * @Author: 一方通行
 * @Date: 2022-04-22
 * @Version:v1.0
 */
public class Number396 {

    public static void main(String[] args) {
        final int i = new Number396().maxRotateFunction1(new int[]{1, 2, 4, 5});
        System.out.println(i);
    }

    public int maxRotateFunction(int[] nums) {
        int f = 0, n = nums.length, numSum = Arrays.stream(nums).sum();
        for (int i = 0; i < n; i++) {
            f += i * nums[i];
        }
        int res = f;
        for (int i = n - 1; i > 0; i--) {
            f += numSum - n * nums[i];
            res = Math.max(res, f);
        }
        return res;
    }


    public int maxRotateFunction1(int[] nums) {
        int sum = 0, F = 0, max = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            //计算 nums总和
            sum += nums[i];
            // F 计算下标 * nums[i] 总和
            F += i * nums[i];
        }
        int dp[] = new int[nums.length];
        dp[0] = F;
        max = dp[0];
        for (int i = 1; i < nums.length; i++) {
            dp[i] = dp[i - 1] + sum - nums.length * nums[nums.length - i];
            max = Math.max(max, dp[i]);

        }
        return max;
    }
}
