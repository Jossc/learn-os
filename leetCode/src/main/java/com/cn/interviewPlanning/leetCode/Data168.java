package com.cn.interviewPlanning.leetCode;

/**
 * @Description: Data168
 * @Author: 一方通行
 * @Date: 2023-12-06
 * @Version:v1.0
 */
public class Data168 {

    /**
     *
     * https://leetcode.cn/problems/reverse-words-in-a-string-ii/solutions/2416792/fan-zhuan-zi-fu-chuan-zhong-de-dan-ci-ii-wzli/?envType=study-plan-v2&envId=premium-algo-100
     * 1.题意要求空间复杂度O(1)，因此必须要在原数组上直接修改；
     * 2.
     * @param str
     */
    public void reverseWords(char[] str) {
        int i = 0;
        for (int j = 0; j < str.length; j++) {
            if (str[j] != ' ') continue;
            reverse(str, i, j);
            i = j + 1;
        }
        reverse(str, i, str.length); // aTbTcT
        reverse(str, 0, str.length); // cba
    }

    private void reverse(char[] str, int i, int j) {
        for (int k = i; k < (i + j) / 2; k++) {
            char tmp = str[k];
            int g = j - 1 - k + i;
            str[k] = str[g];
            str[g] = tmp;
        }
    }

}
