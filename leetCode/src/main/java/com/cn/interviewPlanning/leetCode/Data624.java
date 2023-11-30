package com.cn.interviewPlanning.leetCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description: Data624
 * @Author: 一方通行
 * @Date: 2023-11-30
 * @Version:v1.0
 */
public class Data624 {

    /**
     * 给定 m 个数组，每个数组都已经按照升序排好序了。现在你需要从两个不同的数组中选择两个整数（每个数组选一个）并且计算它们的距离。
     * 两个整数 a 和 b 之间的距离定义为它们差的绝对值 |a-b| 。你的任务就是去找到最大距离
     *
     *
     * 解题思路
     *
     * 1.因为是按照升序排列好,所以可以先把arrays[0] 取出来
     *       int minDistance = arrays.get(0).get(0);
     *       int maxDistance = arrays.get(0).get(n - 1);
     * 2.循环 arrays 取 minDistance maxDistance
     *
     *
     * @param arrays
     * @return
     */
    public int maxDistance(List<List<Integer>> arrays) {
        if (arrays.size() == 0) {
            return -1;
        }
/*        //遍历取 忽略了边界值 应该取 把数组下标是0的先取出来
        int minDistance = 0;
        List<Integer> minDistanceList = new ArrayList<>();
        for (List<Integer> array : arrays) {
            minDistanceList.add(array.get(0));
            //minDistance = Math.min(minDistance, array.get(0));
        }
        Collections.sort(minDistanceList);
        minDistance = minDistanceList.get(0);
        System.out.println("minDistance: " + minDistance);
        int maxDistance = 0;
        for (List<Integer> array : arrays) {
            maxDistance = Math.max(maxDistance, array.get(array.size() - 1));
        }
        System.out.println("maxDistance: " + maxDistance);*/

        int res = 0;
        int n = arrays.get(0).size();
        int minDistance = arrays.get(0).get(0);
        int maxDistance = arrays.get(0).get(n - 1);
        for (int i = 1; i < arrays.size(); i++) {
            n = arrays.get(i).size();
            res = Math.max(res, Math.max(Math.abs(arrays.get(i).get(n - 1) - minDistance),
                    Math.abs(maxDistance - arrays.get(i).get(0))));
            minDistance = Math.min(minDistance,arrays.get(i).get(0));
            maxDistance = Math.max(maxDistance,arrays.get(i).get(n-1));
        }
        return res;
    }
}
