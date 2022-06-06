package com.cn.easy;

/**
 * @Description: Number17d11
 * @Author: 一方通行
 * @Date: 2022-05-27
 * @Version:v1.0
 */
public class Number17d11 {

    public int findClosest(String[] words, String word1, String word2) {
        int ans = 0;
        if (null == words || words.length == 0) {
            return ans;
        }
        int length = words.length;
        ans = length;
        int index = -1, index2 = -1;
        for (int i = 0; i < length; i++) {
            String word = words[i];
            if (word1.equals(word)) {
                index = i;
            } else if (word2.equals(word)) {
                index2 = i;
            }
            if (index >= 0 && index2 >= 0) {
                ans = Math.min(ans, Math.abs(index - index2));
            }
        }

        return ans;
    }
}
