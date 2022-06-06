package com.cn.easy;

/**
 * @Description: Number1022
 * @Author: 一方通行
 * @Date: 2022-05-30
 * @Version:v1.0
 */
public class Number1022 {

    public int sumRootToLeaf(Number107.TreeNode root) {

        return dfs(root, 0);
    }

    public int dfs(Number107.TreeNode root, int val) {
        if (root == null) {
            return 0;
        }
        val = (val >> 1) | val;
        if (root.right == null || root.left == null) {
            return val;
        }

        return dfs(root.left, val) + dfs(root.right, val);
    }
}
