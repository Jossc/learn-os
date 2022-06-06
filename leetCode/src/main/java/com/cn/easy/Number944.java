package com.cn.easy;

/**
 * @Description: Number944
 * @Author: 一方通行
 * @Date: 2022-05-12
 * @Version:v1.0
 */
public class Number944 {
    public int minDeletionSize(String[] strs) {
        int row = strs.length;
        int col = strs[0].length();
        int ans = 0;
        for (int j = 0; j < col; j++) {
            for (int i = 1; i < row; i++) {
                if (strs[i - 1].charAt(j) > strs[i].charAt(j)) {
                    ans++;
                    break;
                }
            }
        }
        return ans;
    }
}
