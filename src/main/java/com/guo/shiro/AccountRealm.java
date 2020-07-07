package com.guo.shiro;

import com.guo.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    // 认证  传进来一个 token
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 获取 AuthController 获取token令牌时输进来的  username password
        // AuthController --》 doLogin(String username,String password)
        //                --》new UsernamePasswordToken(username, SecureUtil.md5(password));
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

        // 用户登录数据 用户名，邮箱,头像，创建时间
        AccountProfile accountProfile = userService.login(usernamePasswordToken.getUsername(),String.valueOf(usernamePasswordToken.getPassword()));

        // accountProfile 放到session 中
        SecurityUtils.getSubject().getSession().setAttribute("profile", accountProfile);

        // 用户认证信息
        // 3个参数    当前用户认证，密钥，用户认证名称
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(accountProfile,token.getCredentials(),getName());

        return info;
    }
}
