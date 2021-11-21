package com.heriec.blogmaster.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.pojo.Category;
import com.heriec.blogmaster.pojo.Tag;
import com.heriec.blogmaster.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Wrapper;
import java.util.List;

@RestController
@RequestMapping("/admin/tag")
@Slf4j
@Api("标签操作")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 查询所有标签
     *
     * @return
     */
    @RequiresAuthentication
    @GetMapping("/list")
    public Result selectList() {
        List<Tag> tags = tagService.selectList();
        if (tags != null) {
            log.info("查询了所有标签");
            log.info(tags.toString());
            return Result.success(tags);
        } else {
            log.info("查询所有标签失败，结果为null");
            return Result.fail("标签为空");
        }
    }

    @GetMapping("/listPage")
    @ApiOperation("标签分页查询")
    @RequiresAuthentication
    public Result selectList(@RequestParam("currentPage") Integer id) {
        Page<Tag> page = new Page<>(id, 6);
        tagService.page(page);
        log.info("查询了标签表第{}页的数据", id);
        return Result.success(page);

    }

    @PostMapping("/update")
    @ApiOperation("更新标签")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result update(@RequestBody Tag tag) {
        boolean b = tagService.updateById(tag);
        if (b) {
            log.info("更新了标签表id为{}的数据", tag.getId());
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @PostMapping("/save")
    @ApiOperation("新增标签")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result save(@RequestBody Tag tag) {
        boolean b = tagService.save(tag);
        if (b) {
            log.info("新增了一个标签");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除标签根据id")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result delete(Integer id) {
        boolean b = tagService.removeById(id);
        if (b) {
            log.info("删除了一个标签");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @GetMapping("/getTagByName/{tagName}")
    @ApiOperation("搜索标签名")
    @RequiresAuthentication
    public Result FindTagByName(@RequestParam("id") Integer id, @PathVariable String tagName) {
        Page<Tag> page = new Page<>(id,6);
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.like("name",tagName);
        tagService.page(page, wrapper);
        if (page != null) {
            log.info("查询了{}个标签",page.getTotal());
            return Result.success(page);
        }
        return Result.fail("操作失败，请联系管理员");
    }
}
