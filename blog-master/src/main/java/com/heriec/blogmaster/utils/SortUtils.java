package com.heriec.blogmaster.utils;

import com.heriec.blogmaster.dto.ArticleDtoClient;

import java.util.List;

public class SortUtils {

    public static void sortArticleByTop(List<ArticleDtoClient> articleDtoClients) {
        for (int i = 0; i < articleDtoClients.size() - 1; i++) {
            for (int j = i ; j < articleDtoClients.size(); j++) {
                if (articleDtoClients.get(i).getTop() == 0 && articleDtoClients.get(j).getTop() == 1) {
                    ArticleDtoClient articleDtoClient = articleDtoClients.get(i);
                    articleDtoClients.set(i, articleDtoClients.get(j));
                    articleDtoClients.set(j, articleDtoClient);
                }
            }
        }
    }
}
