package learn.es.ti;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * author: 一方通行
 */
public class VM {

    public static double calCosSim(Map<String, Double> v1,
                                   Map<String, Double> v2) {
        double sclar = 0.0, nromal = 0.0, nromal2 = 0.0, similarity = 0.0;
        Set<String> v1k = v1.keySet();
        Set<String> v2k = v2.keySet();
        Set<String> both = new HashSet<>();
        both.addAll(v1k);
        both.addAll(v2k);
        System.out.println(both);
        for (String str : both) {
            sclar += v1.getOrDefault(str, 0D) * v2.getOrDefault(str, 0D);
        }
        for (String str : both) {
            nromal += Math.pow(v1.getOrDefault(str, 0D), 2);
        }
        for (String str : both) {
            nromal2 += Math.pow(v2.getOrDefault(str, 0D), 2);
        }
        similarity = sclar / Math.sqrt(nromal * nromal2);
        System.out.println("sclar : " + sclar);
        System.out.println("nromal : " + nromal);
        System.out.println("nromal2 : " + nromal2);
        System.out.println("similarity : " + similarity);
        return similarity;
    }

    public static void main(String[] args) {
        Map<String, Double> m1 = new HashMap<>();
        m1.put("Hello", 1.0);
        m1.put("css", 2.0);
        m1.put("Lucene", 3.0);

        Map<String, Double> m2 = new HashMap<>();
        m2.put("Hello", 1.0);
        m2.put("Word", 2.0);
        m2.put("Hadoop", 3.0);
        m2.put("java", 4.0);
        m2.put("html", 1.0);
        m2.put("css", 2.0);
        calCosSim(m1, m2);
    }
}
