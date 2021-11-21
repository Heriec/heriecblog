package com.heriec.blogmaster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heriec.blogmaster.dto.UserDto;
import com.heriec.blogmaster.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
