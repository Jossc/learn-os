package com.cn.easy;

/**
 * @Description: Number1050
 * @Author: 一方通行
 * @Date: 2022-05-13
 * @Version:v1.0
 */
public class Number1050 {

    public boolean oneEditAway(String a, String b) {
        int n = a.length(), m = b.length();
        if (Math.abs(n - m) > 1) return false;
        if (n > m) return oneEditAway(b, a);
        int i = 0, j = 0, cnt = 0;
        while (i < n && j < m && cnt <= 1) {
            char c1 = a.charAt(i), c2 = b.charAt(j);
            if (c1 == c2) {
                i++;
                j++;
            } else {
                if (n == m) {
                    i++;
                    j++;
                    cnt++;
                } else {
                    j++;
                    cnt++;
                }
            }
        }
        return cnt <= 1;
    }


    public boolean oneEditAway1(String first, String second) {
        int a = first.length(), b = second.length();
        if (Math.abs(a - b) > 1) {
            return false;
        }
        if (b > a) {
            return oneEditAway1(second, first);
        }
        int i = 0, j = 0, cnt = 0;
        while (i < a && j < b && cnt <= 1) {
            char c1 = first.charAt(i), c2 = second.charAt(j);
            if (c1 == c2) {
                i++;
                j++;
            } else {
                if (a == b) {
                    i++;
                    j++;
                    cnt++;
                } else {
                    j++;
                    cnt++;
                }

            }
        }

        return cnt <= 1;
    }
}
