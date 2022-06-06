package com.cn.easy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Description: Solution
 * @Author: 一方通行
 * @Date: 2022-04-25
 * @Version:v1.0
 */
public class Solution {

    Map<Integer, ArrayList<Integer>> map;
    Random random;

    public Solution(int[] nums) {
        map = new HashMap<>();
        random = new Random();
        for (int i = 0; i < nums.length; i++) {
            map.putIfAbsent(nums[i], new ArrayList<Integer>());
            map.get(nums[i]).add(i);
        }
    }

    public int pick(int target) {
        final ArrayList<Integer> integers = map.get(target);
        return integers.get(random.nextInt(integers.size()));
    }
}
