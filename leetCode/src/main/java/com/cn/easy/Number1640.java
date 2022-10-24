package com.cn.easy;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Number1640
 * @Author: 一方通行
 * @Date: 2022-09-22
 * @Version:v1.0
 */
public class Number1640 {

    public boolean canFormArray(int[] arr, int[][] pieces) {
        int n = arr.length, m = pieces.length;
        Map<Integer, Integer> index = new HashMap<Integer, Integer>();
        for (int i = 0; i < m; i++) {
            index.put(pieces[i][0], i);
        }
        for (int i = 0; i < n; ) {
            if (!index.containsKey(arr[i])) {
                return false;
            }
            int j = index.get(arr[i]), len = pieces[j].length;
            for (int k = 0; k < len; k++) {
                if (arr[i + k] != pieces[j][k]) {
                    return false;
                }
            }
            i = i + len;
        }
        return true;
    }
}
