package com.heriec.blogmaster.task;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.heriec.blogmaster.pojo.Article;
import com.heriec.blogmaster.pojo.VisitCount;
import com.heriec.blogmaster.service.ArticleService;
import com.heriec.blogmaster.service.VisitCountService;
import com.heriec.blogmaster.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class VisitCountTask {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    ArticleService articleService;

    @Autowired
    VisitCountService visitCountService;

    /**
     * 定时任务，把redis里面存储的文章阅览信息存储到DB里面
     */
    //@Scheduled(cron = "0/5 * * * * ?   ")
    @Scheduled(cron = "0 0 0/1 * * ?   ")
    public void saveViewCountToDB() {
        log.info("开始执行文章阅览量的定时任务");
        List<Article> articleList = articleService.list();
        for (Article article : articleList) {
            String key = "article:visitCount:" + article.getId();
            Integer hGetViewCount = (Integer) redisUtils.get(key);
            if (hGetViewCount != null) {
                articleService.update(new UpdateWrapper<Article>().eq("id", article.getId()).set("read_num", hGetViewCount));
            }
            //删除redis里面存储的信息
            redisUtils.del(key);
        }
    }

    /**
     * 把redis中的UV PV持久化到数据库
     * 并清空缓存
     */
    // 每一分钟执行一次
     @Scheduled(cron = "0 0 0/1 * * ?   ")
    public void uvpvTask() {
        log.info("进行了总浏览量和访问量定时任务");
        log.info("--------------");
        if (redisUtils.get("blog:PV") != null && redisUtils.sGet("blog:ip:route") != null ){
            Integer perPV = Integer.parseInt((String) redisUtils.get("blog:PV"));
            int length = (int)redisUtils.sGetSetSize("blog:ip:route");
            visitCountService.update(new UpdateWrapper<VisitCount>().eq("id",1).set("pv",perPV+length));
        }
        if(redisUtils.get("blog:UV")!=null&&redisUtils.sGet("blog:ip")!=null){
            Integer i = Integer.parseInt((String)redisUtils.get("blog:UV"));
            int l = (int)redisUtils.sGetSetSize("blog:ip");
            visitCountService.update(new UpdateWrapper<VisitCount>().eq("id",1).set("uv",i+l));
        }
        // 清空缓存
        redisUtils.del("blog:UV");
        redisUtils.del("blog:PV");
        redisUtils.del("blog:ip");
        redisUtils.del("blog:ip:route");
    }
}
