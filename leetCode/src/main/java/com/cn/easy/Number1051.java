package com.cn.easy;

import java.util.Arrays;

/**
 * author: 一方通行
 */
public class Number1051 {
    public int heightChecker(int[] heights) {
        int[] clone = heights.clone();
        Arrays.sort(heights);
        int ans = 0, n = heights.length;
        for (int i = 0; i < n; i++) {
            if (heights[i] != clone[i]) {
                ans++;
            }
        }
        return ans;
    }

}
