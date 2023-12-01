package com.cn.interviewPlanning.leetCode;

import java.util.Arrays;

/**
 * @Description: Data280
 * @Author: 一方通行
 * @Date: 2023-12-01
 * @Version:v1.0
 */
public class Data280 {


    public void wiggleSort(int[] nums) {
        //先排序
        Arrays.sort(nums);
        for (int i = 2; i < nums.length; i += 2) {
            nums[i] = nums[i] ^ nums[i - 1];
            nums[i - 1] = nums[i] ^ nums[i - 1];
            nums[i] = nums[i] ^ nums[i - 1];
        }
    }
}
