package com.cn.easy;

/**
 * @Description: Number875
 * @Author: 一方通行
 * @Date: 2022-06-07
 * @Version:v1.0
 */
public class Number875 {

    public int minEatingSpeed(int[] piles, int h) {
        int maxVal = 1;
        for (int pile : piles) {
            maxVal = Math.max(maxVal, pile);
        }
        int l = 1;
        int r = maxVal;
        while (l < r) {
            int mind = (l +r) / 2;
            if (getTime(piles, mind) > h) {
                l = mind + 1;
            } else {
                r = mind;
            }

        }
        return l;
    }

    //需要向上取整
    public int getTime(int[] piles, int h) {
        int sum = 0;
        for (int pile : piles) {
            sum += (pile + h - 1) / h;
        }
        return sum;
    }

}
