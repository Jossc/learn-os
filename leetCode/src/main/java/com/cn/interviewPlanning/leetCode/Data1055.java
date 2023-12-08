package com.cn.interviewPlanning.leetCode;

/**
 * @Description: Data1055
 * @Author: 一方通行
 * @Date: 2023-12-07
 * @Version:v1.0
 */
public class Data1055 {



    public int shortestWay(String source, String target) {
        char[] str1 = source.toCharArray();
        char[] str2 = target.toCharArray();
        int j = 0;
        int count = 0;
        while (j < str2.length) {
            int prev = j;
            for (int i = 0; i < str1.length; i++) {
                if (j < str2.length && str1[i] == str2[j]) {
                    j++;
                }
            }
            if (prev == j) {
                return -1;
            }
            count++;
        }
        return count;
    }
}
