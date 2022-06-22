package com.cn.easy;

import java.util.Date;

/**
 * author: 一方通行
 */
public class Number513 {
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    int curVal = 0;
    int curHeight = 0;

    public int findBottomLeftValue(TreeNode root) {
        int ch = 0;
        dfs(root, ch);
        return curVal;

    }

    public void dfs(TreeNode treeNode, int h) {
        if (treeNode == null) {
            return;
        }
        h++;
        dfs(treeNode.left, h);
        dfs(treeNode.right, h);
        if (h > curHeight) {
            curHeight = h;
            curVal = treeNode.val;
        }
    }

}
