package com.cn.jvm.gc;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: GcMain
 * @Author: 一方通行
 * @Date: 2021-09-20
 * @Version:v1.0
 */
public class GcMain {

    public static void main(String[] args) {
        List<Byte[]> list = new ArrayList<>();
        int i = 0;
        try {
            while (true) {
                list.add(new Byte[1024 * 1024]);
                i++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("执行了" + i + "次");
        }

    }

    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        int n = nums1.length;
        int nums3[] = new int[n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < nums2.length; j++)
                if (nums1[i] == nums2[j])
                    for (int k = j; k < nums2.length; k++)
                        if (nums2[k] > nums1[i]) {
                            nums3[i] = nums2[k];
                            break;
                        } else{
                            nums3[i] = -1;
                        }

        return nums3;
    }
}
