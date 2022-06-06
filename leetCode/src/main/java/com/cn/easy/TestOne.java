package com.cn.easy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: TestOne
 * @Author: 一方通行
 * @Date: 2021-12-18
 * @Version:v1.0
 */
public class TestOne {
    public static void main(String[] args) {
        String s = "hhh 12345";
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(s);
        final String s1 = matcher.replaceAll("");
        System.out.println(s1);
        System.out.println(s);

    }

    public List<Integer> findNodes(Deper deper, String s) {

        return null;
    }


    public static class Deper {
        private Long id;
        private String name;
        private List<Deper> children;
    }


    public void remove(List<String> src) {
        if (src.size() == 0) {
            return;
        }
        Pattern pattern = Pattern.compile("[^0-9]");
        // 这里应该要考虑下list 是不是超过某些长度
        Map<Integer, List<String>> stringMap = new HashMap<Integer, List<String>>();
        for (String string : src) {
            List<String> newList = new ArrayList<>();
            Matcher matcher = pattern.matcher(string);
            final String key = matcher.replaceAll("");
            newList.add(string);
            stringMap.put(Integer.valueOf(key), newList);
        }

        for (Map.Entry<Integer, List<String>> entry : stringMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                final int size = entry.getValue().size();
                final List<String> value = entry.getValue();
                if (size > 2) {
                    for (int i = 2; i < size; i++) {
                        value.remove(i);
                    }
                } else {
                    value.remove(2);
                }
            }
        }
    }
}
