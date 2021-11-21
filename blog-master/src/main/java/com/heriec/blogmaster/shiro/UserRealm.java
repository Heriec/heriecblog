package com.heriec.blogmaster.shiro;

import com.heriec.blogmaster.pojo.User;
import com.heriec.blogmaster.service.UserService;
import com.heriec.blogmaster.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserRealm extends AuthorizingRealm {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserService userService;


    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     *  授权
     *  把收集到的信息封装成AuthorizationInfo，然后交给SecurityManager
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User)principalCollection.getPrimaryPrincipal();
        User realUser = userService.getUserById(user.getId());
        //获得用户的角色并返回
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addStringPermission(realUser.getRole());
        return authorizationInfo;
    }

    /**
     * 认证 | 登录
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("认证开始！！！！");
        JwtToken jwtToken = (JwtToken) authenticationToken;
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        log.info("认证用户id为{}",userId);
        User user = userService.getUserById(Integer.parseInt(userId));
        if(user==null){
            log.info("认证失败");
            throw new UnknownAccountException("认证失败");
        }
        return  new SimpleAuthenticationInfo(user,jwtToken.getCredentials(),getName());
    }
}
