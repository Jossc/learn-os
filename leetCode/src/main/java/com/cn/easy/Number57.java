package com.cn.easy;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Number57
 * @Author: 一方通行
 * @Date: 2023-08-28
 * @Version:v1.0
 */
public class Number57 {

    public int[][] insert(int[][] intervals, int[] newInterval) {

        int l = newInterval[0];
        int r = newInterval[1];
        boolean pl = false;
        List<int[]> ansList = new ArrayList<int[]>();
        for (int[] interval : intervals) {
            if (interval[0] > r) {
                if (!pl) {
                    ansList.add(new int[]{l, r});
                    pl = true;
                }
                ansList.add(interval);
            } else if (interval[1] < l) {
                ansList.add(new int[]{l, r});
            } else {
                l = Math.min(interval[0], l);
                r = Math.max(interval[1], r);
            }
        }
        if (!pl) {
            ansList.add(new int[]{l, r});
        }
        int[][] ans = new int[ansList.size()][2];
        for (int i = 0; i < ansList.size(); ++i) {
            ans[i] = ansList.get(i);
        }
        return ans;

    }

    public int[][] insert2(int[][] intervals, int[] newInterval) {
        List<int[]> ansList = new ArrayList<int[]>();



        return null;

    }

}
