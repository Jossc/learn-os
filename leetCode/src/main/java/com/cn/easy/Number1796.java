package com.cn.easy;

/**
 * @Description: Number1796
 * @Author: 一方通行
 * @Date: 2022-12-03
 * @Version:v1.0
 */
public class Number1796 {
    public int secondHighest(String s) {
        int first = -1, second = -1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                int num = c - '0';
                if (num > first) {
                    second = first;
                    first = num;
                } else if (num < first && num > second) {
                    second = num;
                }
            }
        }
        return first;
    }
}
