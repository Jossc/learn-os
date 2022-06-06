package com.cn.easy;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @Description: Number102
 * @Author: 一方通行
 * @Date: 2022-05-28
 * @Version:v1.0
 */
public class Number1021 {

    public String removeOuterParentheses(String s) {
        if (null == s) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        Deque<Character> deque = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ')') {
                deque.pop();
            }
            if (!deque.isEmpty()) {
                sb.append(c);
            }
            if (c == '(') {
                deque.push(c);
            }
        }

        return sb.toString();
    }
}
