package com.heriec.blogmaster.task;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.heriec.blogmaster.pojo.Article;
import com.heriec.blogmaster.service.ArticleService;
import com.heriec.blogmaster.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LikeTask {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisUtils redisUtils;

    @Scheduled(cron = "0 0 0/1 * * ?   ")
    public void saveViewCntToDataBase() {
        log.info("进行了文章浏览量定时任务");
        List<Article> articles = articleService.list();
        for (Article article : articles) {
            String key = "like:aid:" + article.getId();
            long l = redisUtils.sGetSetSize(key);
            articleService.update(new UpdateWrapper<Article>().eq("id", article.getId()).set("like_num", l));
        }
    }
}
