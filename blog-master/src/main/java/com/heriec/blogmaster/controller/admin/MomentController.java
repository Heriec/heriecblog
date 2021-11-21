package com.heriec.blogmaster.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.dto.FriendStatusInfo;
import com.heriec.blogmaster.dto.MomentDto;
import com.heriec.blogmaster.pojo.Moment;
import com.heriec.blogmaster.service.MomentService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/admin/moment")
public class MomentController {
    @Autowired
    private MomentService momentService;

    @GetMapping("/list")
    @ApiOperation("动态分页查询")
    @RequiresAuthentication
    public Result selectList(@RequestParam("currentPage") Integer id) {
        Page<Moment> page = new Page<>(id, 6);
        momentService.page(page);
        log.info("查询了动态表第{}页的数据", id);
        return Result.success(page);

    }

    @PutMapping("/update")
    @ApiOperation("更新动态")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result update(@RequestBody Moment moment) {
        System.out.println(moment);
        boolean b = momentService.updateById(moment);
        if (b) {
            log.info("更新了动态表id为{}的数据", moment.getId());
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @PostMapping("/save")
    @ApiOperation("新增动态")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result save(@RequestBody MomentDto momentDto) {
        Moment moment = new Moment();
        BeanUtil.copyProperties(momentDto,moment);
        boolean b = momentService.save(moment);
        if (b) {
            log.info("新增了一个动态");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除动态根据id")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result delete(Integer id) {
        boolean b = momentService.removeById(id);
        if (b) {
            log.info("删除了一个动态");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @GetMapping("/queryMomentByContent/{momentContent}")
    @ApiOperation("搜索动态名")
    @RequiresAuthentication
    public Result FindMomentName(@RequestParam("currentPage") Integer currentPage, @PathVariable String momentContent) {
        Page<Moment> page = new Page<>(currentPage,6);
        QueryWrapper<Moment> wrapper = new QueryWrapper<>();
        wrapper.like("content",momentContent);
        momentService.page(page, wrapper);
        if (page != null) {
            log.info("查询了{}个动态",page.getTotal());
            return Result.success(page);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @PostMapping("/changeStatus")
    @ApiOperation("更新了动态状态")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result updateStatus(@RequestBody Map<String,Integer> map){
        Integer id = map.get("id");
        Integer status = map.get("status");
        UpdateWrapper<Moment> wrapper = new UpdateWrapper<>();
        wrapper.set("status", status).eq("id", id);
        boolean b = momentService.update(wrapper);
        if(b){
            log.info("更新了动态状态");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }
}
