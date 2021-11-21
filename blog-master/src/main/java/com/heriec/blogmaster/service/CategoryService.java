package com.heriec.blogmaster.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heriec.blogmaster.pojo.Category;
import com.heriec.blogmaster.pojo.Tag;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 查询所有分别
     *
     * @return
     */
    List<Category> selectList();

    /**
     * 根据分类id查询分类名字
     *
     * @param list
     * @return
     */
    List<String> selectCategoryById(List<Integer> list);

    List<Integer> selectCategoryByName(List<String> list);

}
