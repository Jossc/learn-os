package com.cn.bpe;


/**
 * {@code @Description: BytePairEncoder}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2025-05-29}
 * {@code @Version:v1.0}
 */

import java.util.stream.Collectors;
import java.util.*;


public class BytePairEncoder {
    private final Map<String, Integer> vocab = new HashMap<>();
    private final PriorityQueue<PairFrequency> pairQueue = new PriorityQueue<>((a, b) -> b.count - a.count);
    private final List<Pair> mergeHistory = new ArrayList<>();

    // 训练模型
    public void train(List<String> corpus, int vocabSize) {
        // 初始化字符级词汇表
        for (String word : corpus) {
            for (String ch : word.split("")) {
                vocab.put(ch, vocab.getOrDefault(ch, 0) + 1);
            }
        }

        // 迭代合并最高频对
        while (vocab.size() < vocabSize) {
            Map<Pair, Integer> pairs = new HashMap<>();

            // 统计所有相邻符号对频率
            for (String word : corpus) {
                List<String> symbols = Arrays.stream(word.split(""))
                        .collect(Collectors.toList());

                for (int i = 0; i < symbols.size() - 1; i++) {
                    Pair p = new Pair(symbols.get(i), symbols.get(i + 1));
                    pairs.put(p, pairs.getOrDefault(p, 0) + 1);
                }
            }

            // 更新优先队列
            pairQueue.clear();
            pairs.forEach((k, v) -> pairQueue.add(new PairFrequency(k, v)));

            if (pairQueue.isEmpty()) break;

            // 合并最高频对
            PairFrequency max = pairQueue.poll();
            String merged = max.pair.first + max.pair.second;

            // 更新词汇表
            vocab.remove(max.pair.first);
            vocab.remove(max.pair.second);
            vocab.put(merged, max.count);
            mergeHistory.add(max.pair);
        }
    }

    // 编码方法
    public List<String> encode(String text) {
        List<String> tokens = new ArrayList<>();
        int pos = 0;

        while (pos < text.length()) {
            int maxLen = 0;
            String best = null;

            // 贪心匹配最长子词
            for (String token : vocab.keySet()) {
                if (text.startsWith(token, pos) && token.length() > maxLen) {
                    maxLen = token.length();
                    best = token;
                }
            }

            if (best != null) {
                tokens.add(best);
                pos += best.length();
            } else {
                tokens.add("<UNK>");
                pos++;
            }
        }
        return tokens;
    }

    // 解码方法
    public String decode(List<String> tokens) {
        String merged = String.join("", tokens);

        // 反向应用合并历史
        for (int i = mergeHistory.size() - 1; i >= 0; i--) {
            Pair p = mergeHistory.get(i);
            merged = merged.replace(p.first + p.second, p.first + " " + p.second);
        }

        return merged.replace(" ", "");
    }

    // 测试用例
    public static void main(String[] args) {
        List<String> corpus = Arrays.asList("hello", "world", "language", "processing");
        BytePairEncoder encoder = new BytePairEncoder();
        encoder.train(corpus, 20);

        String text = "hellolanguageprocessing";
        List<String> encoded = encoder.encode(text);
        System.out.println("Encoded: " + encoded); // ["hello", "language", "processing"]

        String decoded = encoder.decode(encoded);
        System.out.println("Decoded: " + decoded); // "hellolanguageprocessing"
    }

    // 符号对内部类
    private static class Pair {
        String first;
        String second;

        Pair(String f, String s) {
            first = f;
            second = s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return Objects.equals(first, pair.first) &&
                    Objects.equals(second, pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }

    // 符号对频率内部类
    private static class PairFrequency {
        Pair pair;
        int count;

        PairFrequency(Pair p, int c) {
            pair = p;
            count = c;
        }
    }
}