package com.heriec.blogmaster.controller.client;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.dto.ArticleDtoClient;
import com.heriec.blogmaster.dto.TimeLineArticle;
import com.heriec.blogmaster.pojo.Article;
import com.heriec.blogmaster.service.ArticleService;
import com.heriec.blogmaster.service.ArticleTagRelationService;
import com.heriec.blogmaster.service.TagService;
import com.heriec.blogmaster.utils.Constants;
import com.heriec.blogmaster.utils.SortUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/blog")
@Api("展示文章表")
public class ArticleControllerClient {

    @Autowired
    ArticleService articleService;

    @Autowired
    TagService tagService;

    @Autowired
    ArticleTagRelationService articleTagRelationService;

    @GetMapping("/page/{currentPage}")
    @ApiOperation("分页查询博客")
    public Result selectPage(@PathVariable("currentPage")Integer currentPage) {
        Page<Article> page = new Page<>(currentPage, Constants.CLIENT_PAGE_SIZE);
        Page<Article> articlePage = articleService.page(page);
        List<Article> articles = articlePage.getRecords();
        List<ArticleDtoClient> articleDtoClients = new ArrayList<>();
        for (Article article : articles) {
            ArticleDtoClient articleDtoClient = new ArticleDtoClient();
            int index = article.getId();
            BeanUtils.copyProperties(article, articleDtoClient);
            List<Integer> tagId = articleTagRelationService.queryTagByArticleId(article.getId());
            List<Integer> tagIds = null;
            if (tagId != null && tagId.size() != 0) {
                List<Map<String, Object>> maps = articleService.tagList(index);
                articleDtoClient.setTags(maps);
            }

            articleDtoClients.add(articleDtoClient);
            articleService.getReadNumFromRedis(articleDtoClient);
        }
        SortUtils.sortArticleByTop(articleDtoClients);
        //log.info("分页查询了博客内容{}", articleDtoClients.toString());

        Page<ArticleDtoClient> articleInfoPage = new Page<>(currentPage, Constants.CLIENT_PAGE_SIZE);
        BeanUtil.copyProperties(page, articleInfoPage, "records");
        articleInfoPage.setRecords(articleDtoClients);

        return Result.success(articleInfoPage);
    }

    /**
     * 根据id查询博客
     */
    @GetMapping("/article/{id}")
    @ApiOperation("根据id查询博客info")
    public Result selectBlog(@PathVariable("id") Integer id) {
        ArticleDtoClient articleDtoClient = new ArticleDtoClient();
        Article article = articleService.getById(id);
        List<Integer> tagList = articleTagRelationService.queryTagByArticleId(id);
        if (tagList != null && tagList.size() != 0) {
            List<Map<String, Object>> maps = articleService.tagList(id);
            articleDtoClient.setTags(maps);
        }
        BeanUtils.copyProperties(article,articleDtoClient);
        if (articleDtoClient != null)
            articleService.addReadNum(articleDtoClient);
        if (articleDtoClient != null) {
            log.info("前台查询了id为{}的博客info", id);
            return Result.success(articleDtoClient);
        }
        return Result.fail("操作失败");
    }
    /**
     * 降序返回文章信息，用于归档时间线
     */
    @ApiOperation("降序返回文章信息")
    @PostMapping("/timeLine/{currentPage}")
    public Result selectTimeLine(@PathVariable("currentPage")Integer currentPage){
        Page<Article> page = new Page<>(currentPage, Constants.ARTICLE_TIMELINE);
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<Article>().select("id", "publish_time", "title").orderByDesc("publish_time");
        Page<Article> articlePage = articleService.page(page, articleQueryWrapper);
        List<Article> articles = page.getRecords();
        ArrayList<TimeLineArticle> timeLineArticles = new ArrayList<>();
        for(Article article:articles){
            TimeLineArticle timeLineArticle = new TimeLineArticle();
            timeLineArticle.setId(article.getId());
            timeLineArticle.setPublishTime(article.getPublishTime());
            timeLineArticle.setTitle(article.getTitle());
            timeLineArticles.add(timeLineArticle);
        }
        Page<TimeLineArticle> timeLineArticlePage = new Page<>(currentPage,Constants.ARTICLE_TIMELINE);
        BeanUtils.copyProperties(page,timeLineArticlePage);
        timeLineArticlePage.setRecords(timeLineArticles);

        return Result.success(timeLineArticlePage);
    }
    /**
     * 判断文章是否点赞
     */
    @ApiOperation("判断文章是否点赞")
    @GetMapping("/ifLike")
    public Result ifLike(@Param("aid") Integer aid, HttpServletRequest request){
        Boolean ifLike = articleService.getIfLikeFromRedis(aid, request);
        return Result.success("判断了是否有这个ip点赞",ifLike);
    }
}
