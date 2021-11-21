package com.heriec.blogmaster.controller.client;

import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.dto.TimeLineArticle;
import com.heriec.blogmaster.service.ArticleService;
import com.heriec.blogmaster.service.ArticleTagRelationService;
import com.heriec.blogmaster.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Api("前端操作标签表")
public class TagControllerClient {

    @Autowired
    TagService tagService;

    @Autowired
    ArticleTagRelationService articleTagRelationService;

    @Autowired
    ArticleService articleService;

    @ApiOperation("返回所有标签")
    @PostMapping("/tagList")
    public Result selectTagList() {
        List<Map<String, Object>> maps = tagService.listMaps();
        if (maps != null) {
            log.info("前台查询了所有标签");
            return Result.success(maps);
        }
        return Result.fail("操作失败");
    }

    /**
     * 根据标签降序返回文章信息
     */
//    @ApiOperation("根据标签降序返回文章信息")
//    @PostMapping("/tag")
//    public Result selectTagTimeLine(@RequestParam("id") Integer id) {
//        List<Integer> articleIds = articleTagRelationService.queryArticleByTagId(id);
//        List<TimeLineArticle> timeLineArticles = articleService.selectTimeLineArticle(articleIds);
//        if (timeLineArticles != null) {
//            log.info("前台查询标签{}下的文章{}",id,timeLineArticles);
//            return Result.success(timeLineArticles);
//        }
//        return Result.fail("操作失败");
//    }
//    }
    @ApiOperation("根据标签降序返回文章信息")
    @GetMapping("/tag")
    public Result selectTagTimeLine(@RequestParam ("id") Integer id) {
        List<Integer> articleIds = articleTagRelationService.queryArticleByTagId(id);
        log.info(articleIds.toString());
        List<TimeLineArticle> timeLineArticles = articleService.selectTimeLineArticle(articleIds);
        if (timeLineArticles != null) {
            log.info("前台查询标签{}下的文章{}",id,timeLineArticles);
            return Result.success(timeLineArticles);
        }
        return Result.fail("操作失败");
    }
}
