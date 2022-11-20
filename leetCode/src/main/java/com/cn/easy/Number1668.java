package com.cn.easy;

import java.util.Arrays;

/**
 * @Description: Number1668
 * @Author: 一方通行
 * @Date: 2022-11-03
 * @Version:v1.0
 */
public class Number1668 {

    public int maxRepeating(String sequence, String word) {
        int n = sequence.length(), m = word.length();
        if (m > n) {
            return 0;
        }
        // 计算数组
        int mt[] = new int[m];
        Arrays.fill(mt, -1);
        for (int i = 1; i < m; ++i) {
            int j = mt[i - 1];
            while (j != -1 && word.charAt(j + 1) != word.charAt(i)) {
                j = mt[j];
            }
            if (word.charAt(j + 1) == word.charAt(i)) {
                mt[i] = j + 1;
            }
        }
        int[] f = new int[n];
        int j = -1;
        for (int i = 0; i < n; ++i) {
            while (j != -1 && word.charAt(j + 1) != sequence.charAt(i)) {
                j = mt[j];
            }
            if (word.charAt(j + 1) == sequence.charAt(i)) {
                ++j;
                if (j == m - 1) {
                    f[i] = (i >= m ? f[i - m] : 0) + 1;
                    j = mt[j];
                }
            }
        }
        return Arrays.stream(f).max().getAsInt();
    }
}
