package com.cn.easy;

import java.util.Arrays;

/**
 * @Description: Number88
 * @Author: 一方通行
 * @Date: 2022-05-31
 * @Version:v1.0
 */
public class Number88 {

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        for (int i = 0; i != n; i++) {
            nums1[m + i] = nums2[i];
        }
        Arrays.sort(nums1);
    }
}
