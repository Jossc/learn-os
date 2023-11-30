package com.cn.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 两个无序链表，合并成一个有序链表
 *
 * @Description: Code100
 * @Author: 一方通行
 * @Date: 2023-09-08
 * @Version:v1.0
 */
public class Code100 {


    /**
     * 把无序链表转成有序链表
     * <p>
     * 1 先把无序链表转成有序
     * 2 有序链表修改head
     * 3 head 指向排序
     * 4 两个链表合并
     */
    public static Node100 sort(Node100 aNode, Node100 bNode) {
        List<Node100> aNodeList = new ArrayList<>();
        while (aNode != null) {
            aNodeList.add(aNode);
            aNode = aNode.getNext();
        }
        // 排序 按照正序排列 1 2 3 4 5 6 7 8 9 10 11
        Collections.sort(aNodeList, Comparator.comparingInt(Node100::getData));
        List<Node100> bNodeList = new ArrayList<>();
        while (bNode != null) {
            bNodeList.add(bNode);
            bNode = bNode.getNext();
        }
        Collections.sort(bNodeList, Comparator.comparingInt(Node100::getData));

        List<Node100> mList = new ArrayList<>();
        if (aNodeList.size() > 0) {
            mList.addAll(aNodeList);
        }
        if (bNodeList.size() > 0) {
            mList.addAll(bNodeList);
        }
        Collections.sort(mList, Comparator.comparingInt(Node100::getData));
        if (mList.size() == 0 || mList.isEmpty()) {
            return null;
        }
        Node100 newHead = mList.get(0);
        for (int i = 1; i < mList.size(); i++) {
            newHead.setNext(mList.get(i));
            newHead = mList.get(i);
        }
        return newHead;
    }

    public static void main(String[] args) {
        Node100 aNode = new Node100();
        aNode.setData(2);
        Node100 aNode1 = new Node100();
        aNode1.setData(5);
        Node100 aNode2 = new Node100();
        aNode2.setData(3);
        Node100 aNode3 = new Node100();
        aNode3.setData(9);
        aNode.setNext(aNode1);
        aNode1.setNext(aNode2);
        aNode2.setNext(aNode3);
        aNode3.setNext(null);

        Node100 bNode = new Node100();
        bNode.setData(1);
        Node100 bNode1 = new Node100();
        bNode1.setData(5);
        Node100 bNode2 = new Node100();
        bNode2.setData(4);
        Node100 bNode3 = new Node100();
        bNode3.setData(8);
        bNode.setNext(bNode1);
        bNode1.setNext(bNode2);
        bNode3.setNext(bNode3);
        bNode3.setNext(null);
        Node100 sort = sort(aNode, bNode);
        System.out.println(sort.getData());
        while (sort != null) {
            sort = sort.getNext();
            System.out.println(sort.getData());
        }


    }


    /**
     * @Description: Node100
     * @Author: 一方通行
     * @Date: 2023-09-08
     * @Version:v1.0
     */
    public static class Node100 {

        private int value;

        private int data;

        private Node100 next;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getData() {
            return data;
        }

        public void setData(int data) {
            this.data = data;
        }

        public Node100 getNext() {
            return next;
        }

        public void setNext(Node100 next) {
            this.next = next;
        }
    }
}
