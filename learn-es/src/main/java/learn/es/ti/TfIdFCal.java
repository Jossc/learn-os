package learn.es.ti;

import java.util.Arrays;
import java.util.List;

/**
 * author: 一方通行
 */
public class TfIdFCal {

    public double tf(List<String> doc, String term) {
        double termFrequency = 0;
        for (String s : doc) {
            if (s.equalsIgnoreCase(term)) {
                termFrequency++;
            }
        }

        return termFrequency / doc.size();
    }

    public int df(List<List<String>> doc, String term) {
        int n = 0;
        if (term != null && !term.equals("")) {
            for (List<String> strings : doc) {
                for (String word : strings) {
                    if (term.equalsIgnoreCase(word)) {
                        n++;
                        break;
                    }
                }
            }
        } else {
            System.out.println("term 不能为null");
        }
        return n;
    }

    public double idf(List<List<String>> doc, String term) {
        return Math.log(doc.size() / (double) df(doc, term) + 1);
    }

    public double tfIDf(List<String> doc, List<List<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }


    public static void main(String[] args) {
        List<String> doc = Arrays.asList("人工", "智能", "成为", "互联网", "大会", "焦点");
        List<String> doc1 = Arrays.asList("谷歌", "推出", "开源", "人工", "智能", "系统", "工具");

        List<String> doc2 = Arrays.asList("互联网", "的", "未来", "在", "人工", "智能");

        List<String> doc3 = Arrays.asList("谷歌", "开源", "机器", "学习", "工具");
        List<List<String>> documents = Arrays.asList(doc, doc1, doc2, doc3);
        TfIdFCal tfIdFCal = new TfIdFCal();
        System.out.println(tfIdFCal.tf(doc1, "谷歌"));
        System.out.println(tfIdFCal.df(documents, "谷歌"));
        double tf = tfIdFCal.tfIDf(doc1, documents, "谷歌");

        System.out.println("tf : " + tf);


    }

}
