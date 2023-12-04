package com.cn.interviewPlanning.leetCode;

import java.util.HashMap;
import java.util.Objects;

/**
 * @Description: Data1056
 * @Author: 一方通行
 * @Date: 2023-12-02
 * @Version:v1.0
 */
public class Data1056 {

    public static HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

    static {
        map.put(0, 0);
        map.put(1, 1);
        map.put(6, 9);
        map.put(8, 8);
        map.put(9, 6);
    }

    public boolean confusingNumber(int n) {
        int[] arr = new int[]{0,1,-1,-1,-1,-1,9,-1,8,6};
        int m = 0, x = n;
        while(n != 0){
            int temp = n%10;
            if(arr[temp] == -1){
                return false;
            }
            m = m*10 + arr[temp];
            n /= 10;
        }
        if(m == x){
            return false;
        }
        return true;
    }

}
