package com.pc.project.apistarter.utils;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author pengchang
 * @date 2023/08/08 17:14
 **/
public class AlgorithmUtils {
    public static int minDistance(List<String> tagList1, List<String> tagList2) {
        int n = tagList1.size();
        int m = tagList2.size();

        if (n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (!Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1))) {
                    left_down += 1;
                }
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }

    /**
     * 编辑距离算法（用于计算最相似的两个字符串）
     * 原理：https://blog.csdn.net/DBC_121/article/details/104198838
     *
     * @param word1
     * @param word2
     * @return
     */
    public static int minDistance(String word1, String word2) {
        int n = word1.length();
        int m = word2.length();

        if (n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (word1.charAt(i - 1) != word2.charAt(j - 1)) {
                    left_down += 1;
                }
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }


    // 计算余弦相似度
    public static double cosineSimilarity(List<String> tag1, List<String> tag2) {
        // 将两个标签列表转换为向量
        Map<String, Integer> vector1 = getVector(tag1);
        Map<String, Integer> vector2 = getVector(tag2);

        // 计算两个向量的点积
        int dotProduct = 0;
        for (String key : vector1.keySet()) {
            if (vector2.containsKey(key)) {
                dotProduct += vector1.get(key) * vector2.get(key);
            }
        }

        // 计算两个向量的模长
        double magnitude1 = getMagnitude(vector1);
        double magnitude2 = getMagnitude(vector2);

        // 计算余弦相似度
        return dotProduct / (magnitude1 * magnitude2);
    }

    // 将标签列表转换为向量
    public static Map<String, Integer> getVector(List<String> tags) {
        Map<String, Integer> vector = new HashMap<>();
        for (String tag : tags) {
            vector.put(tag, vector.getOrDefault(tag, 0) + 1);
        }
        return vector;
    }

    // 计算向量的模长
    public static double getMagnitude(Map<String, Integer> vector) {
        double magnitude = 0;
        for (int value : vector.values()) {
            magnitude += Math.pow(value, 2);
        }
        return Math.sqrt(magnitude);
    }

    public static void main2(String[] args) {
        String description = "[你好，我是，哈哈]";
        Gson gson = new Gson();
        List<String> descriptionList = gson.fromJson(description, new TypeToken<List<String>>() {
        }.getType());
        System.out.println(descriptionList);

        List<String> tag1 = Lists.newArrayList("11");
        List<String> tag2 = Lists.newArrayList("12");
        List<String> tag3 = Lists.newArrayList("22");

        System.out.println(cosineSimilarity(tag1, tag2));
        System.out.println(cosineSimilarity(tag1, tag3));

        String str1 = "文案，生成", str2 = "网易云文案抓取", str3 = "网易云";
        System.out.println(StrUtil.similar(str3, str2));
    }

    public static void main(String[] args) {
        List<String> list1 = new Gson().fromJson("[示例,测试]", new TypeToken<List<String>>() {
        }.getType());
        List<String> list2 = new Gson().fromJson("[测试]", new TypeToken<List<String>>() {
        }.getType());

        System.out.println(cosineSimilarity(list1, list2));
    }
}
