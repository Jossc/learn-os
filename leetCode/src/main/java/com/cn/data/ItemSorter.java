package com.cn.data;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

/**
 * @Description: ItemSorter
 * @Author: 一方通行
 * @Date: 2023-08-29
 * @Version:v1.0
 */
import java.util.*;

import java.util.*;
import java.util.*;

class Item {
    String type;

    public Item(String type) {
        this.type = type;
    }
}

public class ItemSorter {
    public static List<Item> sortItems(List<Item> items) {
        List<Item> sortedItems = new ArrayList<>();
        Map<String, Integer> typeCount = new HashMap<>();

        int nonProductIndex = -1;
        boolean needArticleOrVideo = false;

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String itemType = item.type;
            int count = typeCount.getOrDefault(itemType, 0);

            if (i == nonProductIndex && itemType.equals("商品")) {
                sortedItems.add(new Item("图文"));
                nonProductIndex = -1;
                typeCount.put("图文", count + 1);
            } else if (count < 3) {
                sortedItems.add(item);
                typeCount.put(itemType, count + 1);

                if (!itemType.equals("商品")) {
                    nonProductIndex = sortedItems.size() - 1;
                }
            } else if (needArticleOrVideo && (itemType.equals("文章") || itemType.equals("视频"))) {
                sortedItems.add(item);
                needArticleOrVideo = false;
            }

            if (i == items.size() - 1 && sortedItems.size() % 4 != 0) {
                needArticleOrVideo = true;
                i = -1;
            }
        }

        return sortedItems;
    }

    public static void main(String[] args) {
        List<Item> items = new ArrayList<>();
        items.add(new Item("商品"));
        items.add(new Item("图文"));
        items.add(new Item("图文"));
        items.add(new Item("视频"));
        items.add(new Item("文章"));
        items.add(new Item("商品"));
        items.add(new Item("商品"));
        items.add(new Item("商品"));
        items.add(new Item("商品"));

        items.add(new Item("商品"));

        items.add(new Item("商品"));

        items.add(new Item("商品"));

        items.add(new Item("商品"));

        items.add(new Item("商品"));

        items.add(new Item("商品"));
        items.add(new Item("商品"));
        items.add(new Item("商品"));


        List<Item> sortedItems = sortItems(items);
        for (Item item : sortedItems) {
            System.out.println(item.type);
        }
    }
}

