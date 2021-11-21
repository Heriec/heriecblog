package com.heriec.blogmaster.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.dto.FriendStatusInfo;
import com.heriec.blogmaster.pojo.FriendsLink;
import com.heriec.blogmaster.service.FriendsLinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api("友链操作")
@RequestMapping("/admin/friends")
public class FriendsLinkController {
    @Autowired
    private FriendsLinkService friendsLinkService;

    @RequiresAuthentication
    @GetMapping("/list")
    @ApiOperation("友链分页查询")
    public Result selectList(@RequestParam("currentPage") Integer id) {
        Page<FriendsLink> page = new Page<>(id, 6);
        friendsLinkService.page(page);
        log.info("查询了友链表第{}页的数据", id);
        return Result.success(page);

    }

    @PostMapping("/update")
    @ApiOperation("更新友链")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result update(@RequestBody FriendsLink friendsLink) {
        boolean b = friendsLinkService.updateById(friendsLink);
        if (b) {
            log.info("更新了友链表id为{}的数据", friendsLink.getId());
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @PostMapping("/save")
    @ApiOperation("新增友链")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result save(@RequestBody FriendsLink friendsLink) {
        boolean b = friendsLinkService.save(friendsLink);
        if (b) {
            log.info("新增了一个友链");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除友链根据id")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result delete(Integer id) {
        boolean b = friendsLinkService.removeById(id);
        if (b) {
            log.info("删除了一个友链");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @GetMapping("/queryFriend/{friendsLinkName}")
    @ApiOperation("搜索友链名")
    @RequiresAuthentication
    public Result FindCategoryName(@RequestParam("currentPage") Integer currentPage, @PathVariable String friendsLinkName) {
        Page<FriendsLink> page = new Page<>(currentPage,6);
        QueryWrapper<FriendsLink> wrapper = new QueryWrapper<>();
        wrapper.like("name",friendsLinkName);
        friendsLinkService.page(page, wrapper);
        if (page != null) {
            log.info("查询了{}个友链",page.getTotal());
            return Result.success(page);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @PostMapping("/status")
    @ApiOperation("更新了友链状态")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result updateStatus(@RequestBody FriendStatusInfo info){
        UpdateWrapper<FriendsLink> wrapper = new UpdateWrapper<>();
        wrapper.set("is_check", info.getStatus()).eq("id", info.getId());
        boolean b = friendsLinkService.update(wrapper);
        if(b){
            log.info("更新了友链状态");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }
}
