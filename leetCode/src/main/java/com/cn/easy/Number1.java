package com.cn.easy;

/**
 * @Description: Number1
 * @Author: 一方通行
 * @Date: 2022-05-31
 * @Version:v1.0
 */
public class Number1 {

    public int[] twoSum(int[] nums, int target) {
        int[] newNum = new int[2];
        for (int i = 0; i < nums.length; i++) {
            for (int j = 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    newNum[0] = i;
                    newNum[1] = j;
                }
            }
        }

        return newNum;
    }
}
