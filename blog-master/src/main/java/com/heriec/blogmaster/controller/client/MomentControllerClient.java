package com.heriec.blogmaster.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.dto.TimeLineArticle;
import com.heriec.blogmaster.dto.TimeLineMoment;
import com.heriec.blogmaster.pojo.Article;
import com.heriec.blogmaster.pojo.Moment;
import com.heriec.blogmaster.service.MomentService;
import com.heriec.blogmaster.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/moment")
@Api("展示动态")
public class MomentControllerClient {


    @Autowired
    private MomentService momentService;
    /**
     * 降序返回动态信息，用于归档时间线
     */
    @ApiOperation("降序返回动态信息")
    @PostMapping("/MomentTimeLine")
    public Result selectMomentTimeLine(){
        QueryWrapper<Moment> wrapper = new QueryWrapper<Moment>().select("id", "content", "update_time").orderByDesc("update_time");
        List<Moment> momentList = momentService.list();
        ArrayList<TimeLineMoment> timeLineArticles = new ArrayList<>();
        for(Moment moment:momentList){
            TimeLineMoment timeLineMoment = new TimeLineMoment();
            timeLineMoment.setId(moment.getId());
            timeLineMoment.setUpdateTime(moment.getUpdateTime());
            timeLineMoment.setContent(moment.getContent());
            timeLineArticles.add(timeLineMoment);
        }
        return Result.success(timeLineArticles);
    }
}
