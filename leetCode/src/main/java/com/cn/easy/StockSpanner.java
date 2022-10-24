package com.cn.easy;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * number 901
 *
 * @Description: StockSpanner
 * @Author: 一方通行
 * @Date: 2022-10-21
 * @Version:v1.0
 */
public class StockSpanner {

    Deque<int[]> easy = null;
    int index = 0;

    public StockSpanner() {
        easy = new ArrayDeque<>();
        easy.push(new int[]{-1, Integer.MAX_VALUE});
    }

    public int next(int price) {
        index++;
        while (price >= easy.peek()[1]) {
            easy.pop();
        }
        int ret = index - easy.peek()[0];
        easy.push(new int[]{index,price});
        return ret;
    }

}
