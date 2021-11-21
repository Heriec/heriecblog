package com.heriec.blogmaster.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heriec.blogmaster.mapper.CategoryMapper;
import com.heriec.blogmaster.pojo.Category;
import com.heriec.blogmaster.pojo.Tag;
import com.heriec.blogmaster.service.CategoryService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> selectList() {
        List<Category> selectList = categoryMapper.selectList(null);
        return selectList;
    }

    @Override
    public List<String> selectCategoryById(List<Integer> list) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.select("name")
                .in("id",list);
        List<Category> categories = categoryMapper.selectList(wrapper);
        List<String> res = new ArrayList<>();
        for(Category category:categories){
            res.add(category.getName());
        }
        return res;
    }

    @Override
    public List<Integer> selectCategoryByName(List<String> list) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.select("id")
                .in("name",list);
        List<Category> categories = categoryMapper.selectList(wrapper);
        List<Integer> res = new ArrayList<>();
        for(Category category:categories){
            res.add(category.getId());
        }
        return res;
    }

}
