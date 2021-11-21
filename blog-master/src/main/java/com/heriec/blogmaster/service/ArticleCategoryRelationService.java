package com.heriec.blogmaster.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heriec.blogmaster.pojo.ArticleCategoryRelation;

import java.io.Serializable;
import java.util.List;

public interface ArticleCategoryRelationService extends IService<ArticleCategoryRelation> {
    List<Integer> queryArticleCategoryId(Serializable id);

    int deleteArticleCategoryByAid(Integer id);
}
