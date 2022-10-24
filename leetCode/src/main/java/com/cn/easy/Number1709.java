package com.cn.easy;

/**
 * @Description: Number1709
 * @Author: 一方通行
 * @Date: 2022-09-28
 * @Version:v1.0
 */
public class Number1709 {

    public int getKthMagicNumber(int k) {
        int ks[] = new int[k];
        int i3 = 0;
        int i5 = 0;
        int i7 = 0;
        for (int i = 1; i < k; i++) {
            int n = Math.min(Math.min(ks[i3] * 3, ks[i5] * 5), ks[i7] * 7);
            if (n % 3 == 0) {
                i3++;
            }
            if (n % 5 == 0) {
                i5++;
            }
            if (n % 7 == 0) {
                i7++;
            }
            ks[i] = n;
        }
        return ks[k - 1];
    }
}
