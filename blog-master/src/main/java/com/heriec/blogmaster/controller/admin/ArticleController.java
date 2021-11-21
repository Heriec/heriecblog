package com.heriec.blogmaster.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.dto.ArticleDto;
import com.heriec.blogmaster.pojo.Article;
import com.heriec.blogmaster.pojo.Category;
import com.heriec.blogmaster.service.ArticleService;
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
@RequestMapping("/admin/article")
@Slf4j
@Api("操作文章接口")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @RequiresAuthentication
    @GetMapping("count")
    @ApiOperation("查询文章总数")
    public Result articleCount(){
        log.info("查询了文章总数");
        Integer countByStatus = articleService.selectCountByStatus();
        return Result.success(countByStatus);
    }

    @RequiresAuthentication
    @GetMapping("list")
    @ApiOperation("查询最新的10篇文章")
    public Result selectList(){
        log.info("查询了最新的10篇文章");
        List<Article> articles = articleService.listByUpdateTimeDesc();
        return Result.success(articles);
    }

    /**
     * 分页查询
     * @param currentPage
     * @return
     */
    @RequiresAuthentication
    @GetMapping("blogList/{currentPage}")
    @ApiOperation("分页查询")
    public Result selectPage(@PathVariable("currentPage")Integer currentPage){
        Page<Article> page = new Page<>(currentPage, 6);
        System.out.println("======================");
        articleService.page(page);
        log.info("查询文章表了第{}页的数据",currentPage);
        return Result.success(page);
    }

    @RequiresAuthentication
    @GetMapping("/searchBlog/{blogTitle}")
    @ApiOperation("搜索文章名")
    public Result FindCategoryName(@RequestParam("currentPage") Integer currentPage, @PathVariable String blogTitle) {
        Page<Article> page = new Page<>(currentPage,6);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.like("title",blogTitle);
        articleService.page(page, wrapper);
        if (page != null) {
            log.info("查询了{}个文章",page.getTotal());
            return Result.success(page);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @RequiresAuthentication
    @GetMapping("/{id}")
    @ApiOperation("根据文章id查询文章细节")
    public Result selectArticleDtoById(@PathVariable Integer id){
        if(id==null)    return Result.success(null);
        ArticleDto articleDto = articleService.selectArticleDtoById(id);
        if(articleDto != null){
            log.info("查询了id为{}的文章的详细信息detail",id);
            return  Result.success(articleDto);
        }
        return Result.fail("查询失败,请联系管理员");
    }

    /**
     * 保存文章到数据库
     */
    @PostMapping("/saveBlog")
    @ApiOperation("保存文章到数据库")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result saveBlog(@RequestBody ArticleDto articleDto){
        boolean b = articleService.saveBlog(articleDto);
        if(b==true){
            log.info("新增了一篇文章");
            return Result.success("操作成功");
        }
        return Result.fail("操作失败，请联系管理员");

    }

    @DeleteMapping("/delete")
    @ApiOperation("删除文章")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result deleteById(@RequestParam("id")Integer id){
        boolean removeById = articleService.removeById(id);
        if(removeById){
            log.info("删除文章表中id为{}的文章",id);
            return Result.success("删除成功",null);
        }
        return Result.fail("删除失败,数据库中查无此项");
    }

    @GetMapping("/top")
    @ApiOperation("文章置顶与否操作")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result topById(@RequestParam("blog_id")Integer id,@RequestParam("is_top")Integer is_top){
        int updateTopById = articleService.updateTopById(id, is_top);

        if(updateTopById==1) {
            log.info("更新文章表中id为{}的文章置顶状态,结果为{}",id,is_top);
            return Result.success("操作成功",null);
        }
        return Result.fail("操作失败,数据库中查无此项");
    }
}
