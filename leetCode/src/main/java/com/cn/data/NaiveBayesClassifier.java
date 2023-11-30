package com.cn.data;

/**
 * @Description: NaiveBayesClassifier
 * @Author: 一方通行
 * @Date: 2023-08-28
 * @Version:v1.0
 */
import java.util.HashMap;
import java.util.Map;

public class NaiveBayesClassifier {
    private Map<String, Integer> classCounts;
    private Map<String, Map<String, Integer>> wordCounts;
    private Map<String, Integer> wordTotalCounts;

    public NaiveBayesClassifier() {
        classCounts = new HashMap<>();
        wordCounts = new HashMap<>();
        wordTotalCounts = new HashMap<>();
    }

    public void train(String[] documents, String[] classes) {
        if (documents.length != classes.length) {
            throw new IllegalArgumentException("Number of documents must be equal to number of classes.");
        }

        for (int i = 0; i < documents.length; i++) {
            String document = documents[i];
            String category = classes[i];

            classCounts.put(category, classCounts.getOrDefault(category, 0) + 1);

            String[] words = document.split("\\s+");
            for (String word : words) {
                wordCounts.putIfAbsent(category, new HashMap<>());
                wordCounts.get(category).put(word, wordCounts.get(category).getOrDefault(word, 0) + 1);
                wordTotalCounts.put(word, wordTotalCounts.getOrDefault(word, 0) + 1);
            }
        }
    }

    public String classify(String document) {
        String bestClass = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (String category : classCounts.keySet()) {
            double score = Math.log(classCounts.get(category) * 1.0 / sum(classCounts.values()));
            String[] words = document.split("\\s+");
            for (String word : words) {
                double wordProbability = (wordCounts.getOrDefault(category, new HashMap<>()).getOrDefault(word, 0) + 1) * 1.0 /
                        (sum(wordCounts.getOrDefault(category, new HashMap<>()).values()) + wordCounts.size());
                wordProbability *= (wordTotalCounts.getOrDefault(word, 0) + 1) * 1.0 /
                        (sum(wordTotalCounts.values()) + wordTotalCounts.size());
                score += Math.log(wordProbability);
            }
            if (bestClass == null || score > bestScore) {
                bestClass = category;
                bestScore = score;
            }
        }

        return bestClass;
    }

    private int sum(Iterable<Integer> values) {
        int sum = 0;
        for (int value : values) {
            sum += value;
        }
        return sum;
    }

    public static void main(String[] args) {
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();

        String[] documents = {
                "Chinese Beijing Chinese",
                "Chinese Chinese Shanghai",
                "Chinese Macao",
                "Tokyo Japan Chinese",
                "Chinese  china"
        };

        String[] classes = {
                "China",
                "China",
                "China",
                "Not China",
                "Chinese"
        };

        classifier.train(documents, classes);

        String testDocument = "Chinese Chinese Chinese Tokyo Japan";
        String predictedClass = classifier.classify(testDocument);

        System.out.println("Predicted class: " + predictedClass);
    }
}
