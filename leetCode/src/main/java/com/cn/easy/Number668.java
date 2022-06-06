package com.cn.easy;

/**
 * @Description: Number668
 * @Author: 一方通行
 * @Date: 2022-05-18
 * @Version:v1.0
 */
public class Number668 {

    public int findKthNumber(int m, int n, int k) {
        int left = 1, right = m * n;
        while (left < right) {
            int mid = (left + right) / 2;
            int count = mid / n * n;
            for (int i = mid / n + 1; i <= m; i++) {
                count += mid / i;
            }
            if (count >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
}
