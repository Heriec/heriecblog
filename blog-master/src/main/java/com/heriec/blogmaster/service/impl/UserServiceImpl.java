package com.heriec.blogmaster.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heriec.blogmaster.dto.UserDto;
import com.heriec.blogmaster.mapper.UserMapper;
import com.heriec.blogmaster.pojo.User;
import com.heriec.blogmaster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByUsername(String username) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username",username));
        return user;
    }

    @Override
    public User getUserById(int id) {
        User user = userMapper.selectById(id);
        return user;
    }

    @Override
    public int updateUserRole(UserDto userDto) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",userDto.getId());
        wrapper.set("role",userDto.getNewRole());
        int update = userMapper.update(null, wrapper);
        return update;
    }

    @Override
    public int updateUserPassword(UserDto userDto) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",userDto.getId());
        wrapper.set("password",userDto.getNewPassword());
        int update = userMapper.update(null, wrapper);
        return update;
    }
}
