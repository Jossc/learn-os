package com.cn.easy;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Number386
 * @Author: 一方通行
 * @Date: 2022-04-18
 * @Version:v1.0
 */
public class Number386 {
    public static void main(String[] args) {
        new Number386().lexicalOrder(25);
    }

    public List<Integer> lexicalOrder(int n) {
        List<Integer> ans = new ArrayList<>();
        for (int i = 0, j = 1; i < n; i++) {
            ans.add(j);
            //寻找相同开头的
            if (j * 10 <= n) {
                j *= 10;
            } else {
                while (j % 10 == 9 || j + 1 > n) {
                    j /= 10;
                }
                j++;
            }
        }
        return ans;
    }
}
