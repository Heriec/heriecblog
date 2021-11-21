package com.heriec.blogmaster.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heriec.blogmaster.dto.UserDto;
import com.heriec.blogmaster.pojo.User;

public interface UserService extends IService<User> {

    /**
     * 用于登录查询用户名
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    /**
     * 根据用户id查询
     *
     * @param id
     * @return
     */
    User getUserById(int id);

    /**
     * 修改用户的角色
     * @param userDto
     * @return
     */
    int updateUserRole(UserDto userDto);

    /**
     * 修改用户的密码
     * @param userDto
     * @return
     */
    int updateUserPassword(UserDto userDto);
}
