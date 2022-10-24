package com.cn.easy;

/**
 * @Description: Mumber0108
 * @Author: 一方通行
 * @Date: 2022-09-30
 * @Version:v1.0
 */
public class Number0108 {

    public void setZeroes(int[][] matrix) { 
        int m = matrix.length, n = matrix[0].length;
        boolean[] broadcast = new boolean[n],clos = new boolean[m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (matrix[i][j] == 0) broadcast[i] = clos[j] = true;
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (broadcast[i] || clos[j]) matrix[i][j] = 0;
            }
        }
    }

}
