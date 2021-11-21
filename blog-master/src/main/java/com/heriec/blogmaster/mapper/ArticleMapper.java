package com.heriec.blogmaster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heriec.blogmaster.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}


