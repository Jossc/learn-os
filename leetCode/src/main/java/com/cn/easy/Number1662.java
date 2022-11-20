package com.cn.easy;

import java.util.Arrays;

/**
 * @Description: Number1662
 * @Author: 一方通行
 * @Date: 2022-11-01
 * @Version:v1.0
 */
public class Number1662 {
    public boolean arrayStringsAreEqual(String[] word1, String[] word2) {
        final String s =
                Arrays.toString(word1).replaceAll(", ", "");
        System.out.println("s ::" + s);

        final String s2 =
                Arrays.toString(word2).replaceAll(", ", "");
        System.out.println("s2 ::" + s2);

        return Arrays.toString(word1).replaceAll(",", "").equals(Arrays.toString(word2).replaceAll(",", ""));
    }
}
