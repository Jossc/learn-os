package com.cn.easy;

/**
 * @Description: Number2027
 * @Author: 一方通行
 * @Date: 2022-12-27
 * @Version:v1.0
 */
public class Number2027 {
    public int minimumMoves(String s) {

        int covered = -1, res = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 'X' && i > covered) {
                res++;
                covered = i + 2;
            }
        }
        return res;
    }
}
