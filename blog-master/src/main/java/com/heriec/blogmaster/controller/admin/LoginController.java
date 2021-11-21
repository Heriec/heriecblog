package com.heriec.blogmaster.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.symmetric.AES;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.dto.LoginDto;
import com.heriec.blogmaster.dto.RegisterDto;
import com.heriec.blogmaster.pojo.User;
import com.heriec.blogmaster.service.UserService;
import com.heriec.blogmaster.utils.AESUtils;
import com.heriec.blogmaster.utils.Constants;
import com.heriec.blogmaster.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/admin")
@Api("用户登录")
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/register")
    @ApiOperation("注册 ")
    public Result register(@Validated @RequestBody RegisterDto registerDto, HttpServletResponse response) throws Exception {
        //解密
        String decrypt = AESUtils.aesDecrypt(registerDto.getPassword());
        log.info("解密的密码为{}",decrypt);
        //开始加密
        String password = new Md5Hash(decrypt, registerDto.getUsername(), 1024).toHex();
        User user = new User();
        BeanUtil.copyProperties(registerDto, user);
        user.setPassword(password);
        user.setRole(Constants.DEFAULT_ROLE);
        user.setAvatar(Constants.DEFAULT_avatar);
        boolean save = userService.save(user);
        if (save){
            log.info("以添加新用户{}",user);
            return Result.success("用户注册成功！",null);
        }
        return Result.fail("注册失败,请联系管理员！");

    }

    @PostMapping("/login")
    @ApiOperation("根据用户名和密码登录")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception {
        User user = userService.getUserByUsername(loginDto.getUsername());
        if(user==null)  {
            log.error("==================用户名不存在====================");
            return Result.fail(400,"用户名或密码错误",null);
        }
        //解密
        String decrypt = AESUtils.aesDecrypt(loginDto.getPassword());
        log.info("解密的密码为{}",decrypt);
        //开始加密
        String newPassword = new Md5Hash(decrypt, loginDto.getUsername(), 1024).toHex();
        if(!user.getPassword().equals(newPassword)){
            log.error("=================用户名或密码错误====================");
            return Result.fail("用户名或密码错误");
        }
        // 给前端返回jwt
        String jwt = jwtUtils.generateToken(user.getId());
        log.info("前端传回的jwt为{}",jwt);
        response.setHeader("Authorization",jwt);
        response.setHeader("Access-control-Expose-Headers", "Authorization");
        HashMap<String, Object> map = new HashMap<>();
        map.put("id",user.getId());
        map.put("username",user.getUsername());
        map.put("nick",user.getNick());
        map.put("avatar",user.getAvatar());
        map.put("introduction",user.getIntroduction());
        map.put("role",user.getRole());
        return Result.success(map);
    }

    @ApiOperation("注销用户")
    @GetMapping("/logout")
    public Result logout(){
//        Subject subject = SecurityUtils.getSubject();
//        log.info(subject.toString()+"------------===================-------------");
        SecurityUtils.getSubject().logout();
        log.info("用户注销");
        return Result.success("注销成功",null);
    }
}
