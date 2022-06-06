package com.cn.easy;

/**
 * @Description: Number1672
 * @Author: 一方通行
 * @Date: 2022-04-14
 * @Version:v1.0
 */
public class Number1672 {

    /**
     *
     * @param accounts
     * @return
     */
    public int maximumWealth(int[][] accounts) {
        int m = accounts.length, n = accounts[0].length, ans = 0;
        for (int i = 0; i < m; i++) {
            int current = 0;
            for (int j = 0; j < n; j++) {
                current += accounts[i][j];
                ans = Math.max(ans, current);
            }
        }
        return ans;
    }
}
