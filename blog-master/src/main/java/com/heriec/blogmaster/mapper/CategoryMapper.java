package com.heriec.blogmaster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heriec.blogmaster.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
