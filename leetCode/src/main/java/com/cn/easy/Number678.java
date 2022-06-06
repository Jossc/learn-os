package com.cn.easy;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @Description: Number678
 * @Author: 一方通行
 * @Date: 2022-05-28
 * @Version:v1.0
 */
public class Number678 {
    public static void main(String[] args) {
        checkValidString("(");
    }

    public static boolean checkValidString(String s) {
        if (s == null) {
            return false;
        }
        int n = s.length();

        Queue<Integer> l = new LinkedList<>();
        Queue<Integer> r = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (c == '(') {
                l.add(i);
            } else if (c == '*') {
                r.add(i);
            } else {
                if (!l.isEmpty()) {
                    l.poll();
                } else if (!r.isEmpty()) {
                    r.poll();
                } else {
                    return false;
                }
            }
        }
        while (!l.isEmpty() && !r.isEmpty()) {
            int Lindex = l.poll();
            int Rindex = r.poll();
            if (Lindex > Rindex) {
                return false;
            }
        }
        return l.isEmpty();
    }
}
