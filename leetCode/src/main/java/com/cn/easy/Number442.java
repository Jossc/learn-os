package com.cn.easy;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Number442
 * @Author: 一方通行
 * @Date: 2022-05-08
 * @Version:v1.0
 */
public class Number442 {

    public List<Integer> findDuplicates(int[] nums) {
        if (nums.length == 0) {
            return null;
        }
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            System.out.println("abs num " + num);
            int index = Math.abs(num) - 1;
            System.out.println("abs index " + index);
            if (nums[index] > 0) {
                System.out.println(" index : " + index + "index value " + nums[index]);
                nums[index] = -nums[index];
            } else {
                list.add(index + 1);
            }
        }
        return list;
    }
}
