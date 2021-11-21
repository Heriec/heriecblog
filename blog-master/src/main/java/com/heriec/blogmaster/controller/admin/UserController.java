package com.heriec.blogmaster.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.dto.UserDto;
import com.heriec.blogmaster.dto.UserSelfModify;
import com.heriec.blogmaster.pojo.Moment;
import com.heriec.blogmaster.pojo.Tag;
import com.heriec.blogmaster.pojo.User;
import com.heriec.blogmaster.service.UserService;
import com.heriec.blogmaster.utils.AESUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@Slf4j
@Api("用户操作")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    @ApiOperation("用户分页查询")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result selectList(@RequestParam("currentPage") Integer currentPage) {
        Page<User> page = new Page<>(currentPage, 6);
        userService.page(page);
        log.info("查询了用户表第{}页的数据", currentPage);
        return Result.success(page);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除某个用户")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result delete(@RequestParam("id") Integer id) {
        boolean b = userService.removeById(id);
        if (b) {
            log.info("删除了某个用户");
            return Result.success(null);
        }
        return Result.fail("操作失败");
    }


    @GetMapping("/queryUsername/{username}")
    @ApiOperation("搜索用户名")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result FindMomentName(@RequestParam("currentPage") Integer currentPage, @PathVariable String username) {
        Page<User> page = new Page<>(currentPage, 6);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("username", username);
        userService.page(page, wrapper);
        if (page != null) {
            log.info("查询了{}个用户", page.getTotal());
            return Result.success(page);
        }
        return Result.fail("操作失败，请联系管理员");
    }

    @PostMapping("/update")
    @ApiOperation("更新用户信息")
    @RequiresAuthentication
    @RequiresPermissions(value = "超级管理员",logical = Logical.AND)
    public Result update(@RequestBody UserDto userDto) {
        boolean ifPassword = userDto.getNewPassword() != null && !userDto.getNewPassword().equals("");
        boolean ifRole = (userDto.getNewRole() != null) && !userDto.getNewRole().equals("");
        if (userDto.getId() > 0) {
            if (ifPassword && ifRole) {
                //解密
                String decrypt = AESUtils.aesDecrypt(userDto.getNewPassword());
                System.out.println(userDto);
                log.info("解密的密码为{}",decrypt);
                //开始加密
                String newPassword = new Md5Hash(decrypt, userDto.getUsername(), 1024).toHex();
                userDto.setNewPassword(newPassword);
                int i = userService.updateUserPassword(userDto);
                int j = userService.updateUserRole(userDto);
                if (i > 0 && j > 0) {
                    log.info("更新了用户id为{}的角色", userDto.getId());
                    log.info("更新了用户id为{}的密码", userDto.getId());
                    return Result.success("角色和密码修改成功！",null);
                }

            }

            else if (ifRole) {
                int i = userService.updateUserRole(userDto);
                if (i > 0) {
                    log.info("更新了用户id为{}的角色", userDto.getId());
                    return Result.success("角色修改成功！",null);
                }
            }
            else if (ifPassword) {
                //解密
                String decrypt = AESUtils.aesDecrypt(userDto.getNewPassword());
                log.info("解密的密码为{}",decrypt);
                //开始加密
                String newPassword = new Md5Hash(decrypt, userDto.getUsername(), 1024).toHex();
                userDto.setNewPassword(newPassword);
                int i = userService.updateUserPassword(userDto);
                if (i > 0) {
                    log.info("更新了用户id为{}的密码", userDto.getId());
                    return Result.success("密码修改成功！",null);
                }
            }
        }
        return Result.fail("操作失败，请联系管理员");
    }


    @RequiresAuthentication
    @PostMapping("/changePassword")
    @ApiOperation("修改个人密码")
    public Result modifyUserSelfPassword(@RequestBody UserSelfModify userSelfModify){
        User user = userService.getUserById(userSelfModify.getId());
        String olddecrypt = AESUtils.aesDecrypt(userSelfModify.getOldPassword());
        //开始加密
        String oldPassword = new Md5Hash(olddecrypt, userSelfModify.getUsername(), 1024).toHex();
        if (!user.getPassword().equals(oldPassword)){
            return Result.fail("请输入正确的旧密码！");
        }

        //解密
        String decrypt = AESUtils.aesDecrypt(userSelfModify.getNewPassword());
        log.info("解密的密码为{}",decrypt);
        //开始加密
        String newPassword = new Md5Hash(decrypt, userSelfModify.getUsername(), 1024).toHex();
        UserDto userDto = new UserDto(user.getId(), newPassword);
        int i = userService.updateUserPassword(userDto);
        if (i > 0) {
            log.info("更新了用户id为{}的密码", userDto.getId());
            return Result.success("密码修改成功！",null);
        }
        return Result.fail("密码修改失败！");
    }

    @RequiresAuthentication
    @PostMapping("/changePersonalCenter")
    @ApiOperation("修改个人中心信息")
    public Result modifyUserPersonalCenter(@RequestBody User user){
        User oldUser = userService.getUserById(user.getId());
        oldUser.setUsername(user.getUsername());
        oldUser.setAvatar(user.getAvatar());
        oldUser.setIntroduction(user.getIntroduction());
        boolean update = userService.update(oldUser, new UpdateWrapper<User>().eq("id",oldUser.getId()));
        if (update) {
            log.info("更新了用户id为{}的信息", user.getId());
            return Result.success("用户信息修改成功！",null);
        }
        return Result.fail("用户信息修改失败！");
    }
}
