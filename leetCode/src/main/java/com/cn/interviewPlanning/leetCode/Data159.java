package com.cn.interviewPlanning.leetCode;

import java.util.Collections;
import java.util.HashMap;

/**
 * @Description: Data159
 * @Author: 一方通行
 * @Date: 2023-12-08
 * @Version:v1.0
 */
public class Data159 {

    public int lengthOfLongestSubstringTwoDistinct(String s) {
        int n = s.length();
        if (n < 3) {
            return n;
        }
        int r = 0;
        int l = 0;
        HashMap<Character, Integer> integerHashMap = new HashMap<>();
        int maxLen = 2;
        while (r < n) {
            integerHashMap.put(s.charAt(r), r++);
            if (integerHashMap.size() == 3) {
                int minx = Collections.min(integerHashMap.values());
                integerHashMap.remove(s.charAt(minx));
                l = minx + 1;
            }
            maxLen = Math.max(maxLen, r - l);
        }
        return maxLen;
    }

}
