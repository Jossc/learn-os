package com.cn.data;

import com.cn.easy.Number078;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @Description: Code078
 * @Author: 一方通行
 * @Date: 2022-05-25
 * @Version:v1.0
 */
public class Code078 {
    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public static class ListNodeCompare implements Comparator<ListNode> {

        @Override
        public int compare(ListNode o1, ListNode o2) {
            return o1.val - o2.val;
        }
    }

    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null) {
            return null;
        }
        PriorityQueue<ListNode> queue = new PriorityQueue<>(new ListNodeCompare());
        for (int i = 0; i < lists.length; i++) {
            if (lists[i] != null) {
                queue.add(lists[i]);
            }
        }
        if (queue.isEmpty()) {
            return null;
        }
        ListNode head = queue.poll();
        ListNode prev = head;
        // 第一个弹出来,把最小的那个的next节点放进去
        if (prev.next != null) {
            queue.add(prev.next);
        }
        while (!queue.isEmpty()) {
            ListNode cur = queue.poll();
            // 修改prev的指针
            prev.next = cur;
            prev = cur;
            if (cur.next != null) {
                queue.add(cur.next);
            }
        }
        return head;
    }

}
