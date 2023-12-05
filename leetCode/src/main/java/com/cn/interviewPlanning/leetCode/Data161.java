package com.cn.interviewPlanning.leetCode;

import java.util.Stack;

/**
 * @Description: Data161
 * @Author: 一方通行
 * @Date: 2023-12-05
 * @Version:v1.0
 */
public class Data161 {
    public boolean isOneEditDistance(String s, String t) {

        int ns = s.length();
        int nt = t.length();
        if (nt - ns > 1) {
            return false;
        }
        if (ns > nt) {
            return isOneEditDistance(t, s);
        }
        for (int i = 0; i < ns; i++) {
            if (s.charAt(i) != t.charAt(i)) {
                // 如果字符串具有相同的长度
                if (ns == nt)
                    return s.substring(i + 1).equals(t.substring(i + 1));
                    // 如果字符串具有不同的长度
                else
                    return s.substring(i).equals(t.substring(i + 1));
            }
        }
        // 如果在 ns 距离上没有差异，则仅当 t 有多一个字符时，字符串才有一次编辑。
        return (ns + 1 == nt);

    }
}
