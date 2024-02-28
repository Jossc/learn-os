package com.cn.interviewPlanning.leetCode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Data430
 * @Author: 一方通行
 * @Date: 2023-12-11
 * @Version:v1.0
 */
public class Data340 {

    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        int ans = 0;
        Map<Character, Integer> map = new HashMap<>();
        int l = 0;
        for (int i = 0; i < s.length(); i++) {
            map.put(s.charAt(i), i);
            if (map.size() > k) {
                int p = Collections.min(map.values());
                map.remove(s.charAt(p));
                l = p + 1;
            }
            ans = Math.max(ans, i - l + 1);
        }
        return ans;
    }
}
