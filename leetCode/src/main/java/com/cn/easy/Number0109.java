package com.cn.easy;

/**
 * @Description: Number0109
 * @Author: 一方通行
 * @Date: 2022-09-29
 * @Version:v1.0
 */
public class Number0109 {

    public boolean isFlipedString(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }
        String ss = s2 + s2;
        return ss.contains(s1);
    }
}
