package com.cn.easy;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @Description: Number078
 * @Author: 一方通行
 * @Date: 2022-05-24
 * @Version:v1.0
 */
public class Number078 {
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

    public static class ListNodeComparator implements Comparator<ListNode> {

        @Override
        public int compare(ListNode o1, ListNode o2) {
            return o1.val - o2.val;
        }
    }

    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null) {
            return null;
        }

        PriorityQueue<ListNode> queue = new PriorityQueue<>(new ListNodeComparator());
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
        if (prev.next != null) {
            queue.add(prev.next);
        }
        while (!queue.isEmpty()) {
            ListNode cur = queue.poll();
            prev.next = cur;
            prev = cur;
            if (cur.next != null) {
                queue.add(cur.next);
            }
        }
        return head;
    }

}
