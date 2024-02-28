package com.cn.interviewPlanning.leetCode;

import java.util.HashSet;

/**
 * @Description: Data1100
 * @Author: 一方通行
 * @Date: 2023-12-14
 * @Version:v1.0
 */
public class Data1100 {

    public int numKLenSubstrNoRepeats(String s, int k) {
        int count = 0;
        HashSet<Character> set = new HashSet<>();
        for (int i = 0; i <= s.length() - k; i++) {
            set.clear();
            boolean isValid = true;
            for (int j = 0; j < k; j++) {
                char ch = s.charAt(i + j);
                if (set.contains(ch)) {
                    isValid = false;
                    break;
                }
                set.add(ch);
            }
            if (isValid) {
                count++;
            }
        }
        return count;
    }

}
