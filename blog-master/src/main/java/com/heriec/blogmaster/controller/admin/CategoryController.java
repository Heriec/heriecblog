package com.heriec.blogmaster.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.pojo.Category;
import com.heriec.blogmaster.pojo.Tag;
import com.heriec.blogmaster.service.CategoryService;
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
@RequestMapping("/admin/category")
@Slf4j
@Api("分类操作")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类
     *
     * @return
     */
    @RequiresAuthentication
    @GetMapping("/list")
    @ApiOperation("查询所有分类")
    public Result selectList() {
        List<Category> categories = categoryService.selectList();
        if (categories != null) {
            log.info("查询了所有分类");
            log.info(categories.toString());
            return Result.success(categories);
        } else {
            log.info("查询所有分类失败，结果为null");
            return Result.fail("分类为空");
        }
    }

    @RequiresAuthentication
    @GetMapping("/listPage")
    @ApiOperation("分类分页查询")
    public Result selectList(@RequestParam("currentPage") Integer id) {
        Page<Category> page = new Page<>(id, 6);
        categoryService.page(page);
        log.info("查询了分类表第{}页的数据", id);
        return Result.success(page);

    }

    @PostMapping("/update")
    @ApiOperation("更新分类")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result update(@RequestBody Category category) {
        boolean b = categoryService.updateById(category);
        if (b) {
            log.info("更新了分类表id为{}的数据", category.getId());
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @PostMapping("/save")
    @ApiOperation("新增分类")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result save(@RequestBody Category category) {
        boolean b = categoryService.save(category);
        if (b) {
            log.info("新增了一个分类");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除分类根据id")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result delete(Integer id) {
        boolean b = categoryService.removeById(id);
        if (b) {
            log.info("删除了一个分类");
            return Result.success(null);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @GetMapping("/getCateGoryByName/{categoryName}")
    @ApiOperation("搜索分类名")
    @RequiresAuthentication
    public Result FindCategoryName(@RequestParam("currentPage") Integer currentPage, @PathVariable String categoryName) {
        Page<Category> page = new Page<>(currentPage,6);
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.like("name",categoryName);
        categoryService.page(page, wrapper);
        if (page != null) {
            log.info("查询了{}个分类",page.getTotal());
            return Result.success(page);
        }
        return Result.fail("操作失败，请联系管理员");
    }
}
