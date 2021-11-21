package com.heriec.blogmaster.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heriec.blogmaster.pojo.ArticleCategoryRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

@Mapper
public interface ArticleCategoryRelationMapper extends BaseMapper<ArticleCategoryRelation> {
}
