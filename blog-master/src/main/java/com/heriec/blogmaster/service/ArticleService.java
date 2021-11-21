package com.heriec.blogmaster.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heriec.blogmaster.dto.ArticleDto;
import com.heriec.blogmaster.dto.ArticleDtoClient;
import com.heriec.blogmaster.dto.TimeLineArticle;
import com.heriec.blogmaster.pojo.Article;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ArticleService extends IService<Article>{

    List<Article> listByUpdateTimeDesc();

    Integer selectCountByStatus();
    // 根据id查询文章细节
    ArticleDto selectArticleDtoById(Serializable id);

    // 保存文章
    boolean saveBlog(ArticleDto articleDto);

    int updateTopById(Integer id,Integer top);

     List<Map<String,Object>> tagList(Serializable id);

//     List<Map<String,Object>> categoryList(Serializable id);

    boolean addReadNum(ArticleDtoClient articleDtoClient);

    void getReadNumFromRedis(ArticleDtoClient articleDtoClient);

    List<TimeLineArticle> selectTimeLineArticle(List<Integer> articleIds);

    Boolean getIfLikeFromRedis(Integer aid, HttpServletRequest request);

}
