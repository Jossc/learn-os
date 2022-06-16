package com.cn.easy;

import java.util.Arrays;

/**
 * @Description: Number719
 * @Author: 一方通行
 * @Date: 2022-06-16
 * @Version:v1.0
 */
public class Number719 {
    public int smallestDistancePair(int[] nums, int k) {
        if (nums.length == 0) {
            return 0;
        }
        Arrays.sort(nums);
        int l = 0, r = (int) 1e6;
        while (l < r) {
            int mid = l + r << 1;
            if (checkNum(nums, mid) >= k) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }
        return r;
    }

    public int checkNum(int[] nums, int x) {
        int ans = 0, n = nums.length;
        for (int i = 0, j = 1; i < n; i++) {
            while (j < n && nums[j] - nums[i] <= x) j++;
            ans += j - i - 1;
        }
        return ans;
    }


}
