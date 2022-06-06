package com.cn.easy;

/**
 * @Description: Number283
 * @Author: 一方通行
 * @Date: 2022-04-14
 * @Version:v1.0
 */
public class Number283 {
    public void moveZeroes(int[] nums) {
        int index = 0;
        for (int num : nums) {
            if (num != 0) {
                nums[index++] = num;
            }
        }
        for (int i = index; i < nums.length; i++) {
            //从第index 下赋值为-
            nums[i] = 0;
        }
    }
}
