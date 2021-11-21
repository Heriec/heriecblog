package com.heriec.blogmaster.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heriec.blogmaster.pojo.ArticleTagRelation;

import java.io.Serializable;
import java.util.List;

public interface ArticleTagRelationService extends IService<ArticleTagRelation> {
    List<Integer> queryTagByArticleId(Serializable id);

    List<Integer> queryArticleByTagId(Serializable id);

    int deleteArticleTagByAid(Integer id);
}
