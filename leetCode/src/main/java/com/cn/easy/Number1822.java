package com.cn.easy;

/**
 * @Description: Number1822
 * @Author: 一方通行
 * @Date: 2022-10-27
 * @Version:v1.0
 */
public class Number1822 {

    public int arraySign(int[] nums) {
        int size = 0;
        for (int num : nums) {
            if (num == 0) {
                return 0;
            }
            if (num < 0) {
                size++;
            }

        }
        return size % 2 == 0 ? -1 : 1;
    }
}
