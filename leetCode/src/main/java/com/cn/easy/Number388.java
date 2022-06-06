package com.cn.easy;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Number388
 * @Author: 一方通行
 * @Date: 2022-04-20
 * @Version:v1.0
 */
public class Number388 {

    public int lengthLongestPath(String input) {
        Map<Integer, String> map = new HashMap<>();
        int n = input.length();
        String ans = null;
        for (int i = 0; i < n; ) {
            int level = 0;
            //检测当前文件的深度
            while (i < n && input.charAt(i) == '\t' && ++level >= 0) {
                i++;
            }
            int j = i;
            boolean isDir = true;
            //统计当前文件名的长度
            while (j < n && input.charAt(j) != '\n') {
                if (input.charAt(j++) == '.')
                    isDir = false;
            }
            String cur = input.substring(i, j);
            String prev = map.getOrDefault(level - 1, null);
            String path = prev == null ? cur : prev + "/" + cur;
            if (isDir) {
                map.put(level, path);
            } else if (ans == null || path.length() > ans.length()) {
                ans = path;
            }
            i = j + 1;
        }
        return ans == null ? 0 : ans.length();
    }
}
