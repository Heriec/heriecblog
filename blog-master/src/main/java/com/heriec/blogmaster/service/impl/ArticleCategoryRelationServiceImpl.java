package com.heriec.blogmaster.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heriec.blogmaster.mapper.ArticleCategoryRelationMapper;
import com.heriec.blogmaster.pojo.ArticleCategoryRelation;
import com.heriec.blogmaster.pojo.ArticleTagRelation;
import com.heriec.blogmaster.service.ArticleCategoryRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleCategoryRelationServiceImpl extends ServiceImpl<ArticleCategoryRelationMapper, ArticleCategoryRelation> implements ArticleCategoryRelationService {
    @Autowired
    ArticleCategoryRelationMapper articleCategoryRelationMapper;

    /**
     * 根据文章id查询分类id list
     * @param id
     * @return
     */
    @Override
    public List<Integer> queryArticleCategoryId(Serializable id) {
        QueryWrapper<ArticleCategoryRelation> wrapper = new QueryWrapper<>();
        wrapper.select("category_id").eq("article_id",id);
        List<ArticleCategoryRelation> articleCategoryRelations = articleCategoryRelationMapper.selectList(wrapper);
        ArrayList<Integer> list = new ArrayList<>();
        for (ArticleCategoryRelation articleCategoryRelation : articleCategoryRelations) {
            list.add(articleCategoryRelation.getCategoryId());
        }
        return list;
    }

    @Override
    public int deleteArticleCategoryByAid(Integer id) {
        QueryWrapper<ArticleCategoryRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id",id);
        int delete = articleCategoryRelationMapper.delete(wrapper);
        return delete;
    }

}
