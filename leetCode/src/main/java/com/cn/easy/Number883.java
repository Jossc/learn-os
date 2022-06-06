package com.cn.easy;

/**
 * @Description: Number883
 * @Author: 一方通行
 * @Date: 2022-04-26
 * @Version:v1.0
 */
public class Number883 {
    public int projectionArea(int[][] grid) {
        int x = 0, y = 0, z = 0;
        for (int i = 0; i < grid.length; i++) {
            int a = 0, b = 0;
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j] > 0) {
                    x++;
                }
                a = Math.max(a, grid[i][j]);
                b = Math.max(b, grid[j][i]);
            }
            y += a;
            z += b;
        }
        return x + y + z;
    }
}
