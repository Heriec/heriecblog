package com.heriec.blogmaster.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heriec.blogmaster.dto.ArticleDto;
import com.heriec.blogmaster.dto.ArticleDtoClient;
import com.heriec.blogmaster.dto.TimeLineArticle;
import com.heriec.blogmaster.mapper.ArticleMapper;
import com.heriec.blogmaster.pojo.*;
import com.heriec.blogmaster.service.*;
import com.heriec.blogmaster.utils.IPUtils;
import com.heriec.blogmaster.utils.RedisUtils;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    TagService tagService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ArticleTagRelationService articleTagRelationService;

    @Autowired
    ArticleCategoryRelationService articleCategoryRelationService;

    @Override
    public List<Article> listByUpdateTimeDesc() {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("update_time");
        wrapper.last("limit 10");
        List<Article> articles = articleMapper.selectList(wrapper);
        return articles;
    }

    @Override
    public Integer selectCountByStatus() {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("article_status", 1);
        Integer count = articleMapper.selectCount(wrapper);
        return count;
    }

    @Override
    public ArticleDto selectArticleDtoById(Serializable id) {
        List<Integer> categoryId = articleCategoryRelationService.queryArticleCategoryId(id);
        log.info("查询了id为{}的文章的分类序号为->{}", id, categoryId.toString());
        List<String> categoryList = null;
        if (categoryId != null && categoryId.size() != 0) {
            categoryList = categoryService.selectCategoryById(categoryId);
            log.info("查询了分类序号为{}的分类行->{}", categoryId.toString(), categoryList.toString());

        }

        List<Integer> tagId = articleTagRelationService.queryTagByArticleId(id);
        log.info("查询了id为{}的文章的标签序号为->{}", id, tagId.toString());
        List<String> tagList = null;
        if (tagId != null && tagId.size() != 0) {
            tagList = tagService.selectTagById(tagId);
            log.info("查询了标签序号为{}的标签行->{}", tagId.toString(), tagList.toString());
        }


        //查询文章
        Article article = articleMapper.selectById(id);
        log.info("查询了文章序号为{}的文章{}", id, article.getTitle());
        ArticleDto articleDto = new ArticleDto();
        BeanUtil.copyProperties(article, articleDto, false);
        articleDto.setCategories((ArrayList) categoryList);
        articleDto.setTags((ArrayList) tagList);
        return articleDto;
    }

    /**
     * 保存博客
     *
     * @param articleDto
     * @return
     */
    @Override
    public boolean saveBlog(ArticleDto articleDto) {
        Article article = new Article();
        BeanUtil.copyProperties(articleDto, article);
        Integer id = articleDto.getId();
        if (id != null) {
            int deleteCid = articleCategoryRelationService.deleteArticleCategoryByAid(article.getId());
            log.info("删除了id为{}文章的分类个数{}", article.getId(), deleteCid);
            int deleteTid = articleTagRelationService.deleteArticleTagByAid(article.getId());
            log.info("删除了id为{}文章的标签个数{}", article.getId(), deleteTid);
            articleMapper.updateById(article);
        } else {
            articleMapper.insert(article);
        }

        List<String> tags = articleDto.getTags();
        List<String> categories = articleDto.getCategories();
        List<Integer> tagIds = null;
        if (tags != null && tags.size() != 0) {
            tagIds = tagService.selectIdByName(tags);
            log.info("查询到tagsIds{}", tagIds);

            //为ArticleTagRelation表插入数据
            List<ArticleTagRelation> articleTagRelations = new ArrayList<>();
            for (Integer tagId : tagIds) {
                articleTagRelations.add(new ArticleTagRelation(article.getId(), tagId));
            }
            articleTagRelationService.saveBatch(articleTagRelations);
        }
        List<Integer> categoryIds = null;
        if (categories != null && categories.size() != 0) {
            categoryIds = categoryService.selectCategoryByName(categories);
            log.info("查询到categoriesIds{}", categoryIds);

            //为ArticleCategoryRelation插入数据
            ArrayList<ArticleCategoryRelation> articleCategoryRelations = new ArrayList<>();
            for (Integer categoryId : categoryIds) {
                articleCategoryRelations.add(new ArticleCategoryRelation(article.getId(), categoryId));
            }
            articleCategoryRelationService.saveBatch(articleCategoryRelations);
        }


        return true;
    }

    @Override
    public int updateTopById(Integer id, Integer top) {
        Article article = articleMapper.selectById(id);
        UpdateWrapper<Article> wrapper = new UpdateWrapper<>();
        article.setTop(top);
        wrapper.eq("id", id);
        int update = articleMapper.update(article, wrapper);
        return update;
    }

    @Override
    public List<Map<String, Object>> tagList(Serializable id) {
        List<Integer> tagIdList = articleTagRelationService.queryTagByArticleId(id);
        Wrapper<Tag> wrapper = new QueryWrapper<Tag>().in("id", tagIdList);
        List<Map<String, Object>> maps = tagService.listMaps(wrapper);
        return maps;
    }


    @Override
    public boolean addReadNum(ArticleDtoClient articleDtoClient) {
        String key = "article:viewCount:" + articleDtoClient.getId();
        Integer ViewCount = (Integer) redisUtils.get(key);
        if (ViewCount != null) {
            articleDtoClient.setReadNum(ViewCount + 1);
        } else {
            articleDtoClient.setReadNum(articleDtoClient.getReadNum() + 1);
        }
        boolean set = redisUtils.set(key, articleDtoClient.getReadNum());
        return set;
    }

    @Override
    public void getReadNumFromRedis(ArticleDtoClient articleDtoClient) {
        String key = "article:viewCount:" + articleDtoClient.getId();
        Integer ViewCount = (Integer) redisUtils.get(key);
        if (ViewCount != null) {
            articleDtoClient.setReadNum(ViewCount);
        } else {
            redisUtils.set(key, articleDtoClient.getReadNum());
        }
    }

    @Override
    public List<TimeLineArticle> selectTimeLineArticle(List<Integer> articleIds) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.select("id", "publish_time", "title").in("id", articleIds).orderByDesc("publish_time");
        List<Article> articles = articleMapper.selectList(wrapper);
        List<TimeLineArticle> timeLineArticles = new ArrayList<>();
        for (Article article : articles) {
            TimeLineArticle timeLineArticle = new TimeLineArticle();
            timeLineArticle.setId(article.getId());
            timeLineArticle.setPublishTime(article.getPublishTime());
            timeLineArticle.setTitle(article.getTitle());
            timeLineArticles.add(timeLineArticle);
        }
        return timeLineArticles;
    }

    @Override
    public Boolean getIfLikeFromRedis(Integer aid, HttpServletRequest request) {
        String realIp = IPUtils.getRealIp(request);
        boolean hasKey = redisUtils.sHasKey("like:aid:" + aid, realIp);
        if (hasKey) {
            redisUtils.setRemove("like:aid:" + aid, realIp);
        } else {
            redisUtils.sSet("like:aid:" + aid, realIp);
        }
        return !hasKey;
    }
}
