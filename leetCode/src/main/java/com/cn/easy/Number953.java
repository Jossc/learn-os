package com.cn.easy;

/**
 * @Description: Number953
 * @Author: 一方通行
 * @Date: 2022-05-17
 * @Version:v1.0
 */
public class Number953 {
    public boolean isAlienSorted(String[] words, String order) {
        int[] index = new int[26];
        for (int i = 0; i < order.length(); i++) {
            index[order.charAt(i) - 'a'] = i;
        }

        for (int i = 0; i < words.length; i++) {
            boolean temp = false;
            for (int j = 0; j < words[i - 1].length() && j < words[i].length(); j++) {
                int prev = index[words[i - 1].charAt(j) - 'a'];
                int curr = index[words[i].charAt(j) - 'a'];
                if (prev < curr) {
                    temp = true;
                    break;
                } else if (prev > curr) {
                    return false;
                }
            }
            if (!temp) {
                if (words[i - 1].length() > words[i].length()) {
                    return false;
                }
            }

        }
        return true;
    }
}
