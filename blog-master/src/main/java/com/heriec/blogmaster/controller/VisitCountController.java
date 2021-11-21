package com.heriec.blogmaster.controller;

import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.pojo.VisitCount;
import com.heriec.blogmaster.service.VisitCountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@Api("访问量操作")
public class VisitCountController {

    @Autowired
    private VisitCountService visitCountService;

    @GetMapping("/staticUP")
    @ApiOperation("统计uv pv")
    public Result staticUP(@Param("route") String route, HttpServletRequest request){
        VisitCount visitCount = visitCountService.staticUP(route, request);
        return Result.success(visitCount);
    }

    @GetMapping("/admin/visitCount/count")
    @ApiOperation("统计uv pv")
    public Result selectUP(){
        // 后台管理从数据库mysql获取，不从redis
        VisitCount byId = visitCountService.getById(1);
        if (byId!=null) return Result.success(byId);
        return Result.fail("操作失败");
    }
}
