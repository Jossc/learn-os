package com.cn.easy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Number228
 * @Author: 一方通行
 * @Date: 2023-08-26
 * @Version:v1.0
 */
public class Number228 {


    public List<String> summaryRanges(int[] nums) {
        List<String> resultList = new ArrayList<String>();
        int i = 0;
        while (i < nums.length) {
            int pointA = i;
            i ++;
            while (i < nums.length && nums[i-1]+1==nums[i]){
                i++;
            }
            int pointB = i-1;
            StringBuffer sb  = new StringBuffer();
            StringBuffer temp=new StringBuffer(Integer.toString(nums[pointA]));
            if(pointA<pointB){
                temp.append("->");
                temp.append(Integer.toString(nums[pointB]));
            }
            resultList.add(temp.toString());
        }

        return resultList;
    }
}
