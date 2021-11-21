package com.heriec.blogmaster.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heriec.blogmaster.mapper.ArticleTagRelationMapper;
import com.heriec.blogmaster.pojo.ArticleCategoryRelation;
import com.heriec.blogmaster.pojo.ArticleTagRelation;
import com.heriec.blogmaster.service.ArticleTagRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleTagRelationServiceImpl extends ServiceImpl<ArticleTagRelationMapper, ArticleTagRelation> implements ArticleTagRelationService {
    @Autowired
    ArticleTagRelationMapper articleTagRelationMapper;

    @Override
    public List<Integer> queryTagByArticleId(Serializable id) {
        QueryWrapper<ArticleTagRelation> wrapper = new QueryWrapper<>();
        wrapper.select("tag_id").eq("article_id",id);
        List<ArticleTagRelation> articleTagRelations = articleTagRelationMapper.selectList(wrapper);
        ArrayList<Integer> list = new ArrayList<>();
        for (ArticleTagRelation articleTagRelation : articleTagRelations) {
            list.add(articleTagRelation.getTagId());
        }
        return list;
    }

    @Override
    public List<Integer> queryArticleByTagId(Serializable id) {
        QueryWrapper<ArticleTagRelation> wrapper = new QueryWrapper<>();
        wrapper.select("article_id").eq("tag_id",id);
        List<ArticleTagRelation> articleTagRelations = articleTagRelationMapper.selectList(wrapper);
        ArrayList<Integer> list = new ArrayList<>();
        for (ArticleTagRelation articleTagRelation : articleTagRelations) {
            list.add(articleTagRelation.getArticleId());
        }
        return list;
    }

    @Override
    public int deleteArticleTagByAid(Integer id) {
        QueryWrapper<ArticleTagRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id",id);
        int delete = articleTagRelationMapper.delete(wrapper);
        return delete;
    }

}
