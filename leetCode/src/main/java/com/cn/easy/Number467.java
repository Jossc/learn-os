package com.cn.easy;

/**
 * @Description: Number467
 * @Author: 一方通行
 * @Date: 2022-05-25
 * @Version:v1.0
 */
public class Number467 {

    public int findSubstringInWraproundString(String p) {
        int ans = 0;
        if (p == null) {
            return ans;
        }
        char[] chars = p.toCharArray();
        int n = chars.length;
        int[] max = new int[26];
        max[chars[0] - 'a']++;
        for (int i = 1, j = 1; i < n; i++) {
            int c = chars[i] - 'a';
            int p1 = chars[i - 1] - 'a';
            if ((p1 == 25 && c == 0) || p1 + 1 == c) {
                j++;
            } else {
                j = 1;
            }
            max[c] = Math.max(max[c], j);
        }
        for (int i = 0; i < 26; i++) {
            ans += max[i];
        }
        return ans;
    }
}
