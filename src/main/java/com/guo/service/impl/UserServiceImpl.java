package com.guo.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guo.common.lang.Result;
import com.guo.entity.User;
import com.guo.mapper.UserMapper;
import com.guo.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.shiro.AccountProfile;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.omg.CORBA.portable.UnknownException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    JavaMailSenderImpl mailSender;

    // 完成注册
    @Override
    public Result register(User user) {
        int count = this.count(new QueryWrapper<User>()
                .eq("email", user.getEmail())
                .or()
                .eq("username", user.getUsername())
        );

        if (count > 0)
            return Result.fail("用户名或邮箱已存在");

        // 注册信息添加到数据库
        User userTemp = new User();
        userTemp.setUsername(user.getUsername());
        // 密码加密  使用 hutool 的密码加密工具
        userTemp.setPassword(SecureUtil.md5(user.getPassword()));
        userTemp.setEmail(user.getEmail());
        // 设置一些注册默认属性
        userTemp.setCreated(new Date());
        userTemp.setGender("1");
        userTemp.setPoint(0);
        userTemp.setVipLevel(0);
        userTemp.setCommentCount(0);
        userTemp.setPostCount(0);
        userTemp.setAvatar("/res/images/avatar/default.png");

        this.save(userTemp);

        return Result.success();
    }

    /**
     *  用户登录数据
     */
    @Override
    public AccountProfile login(String username, String password) {

        // 根据用户名获取记录
        User user = this.getOne(new QueryWrapper<User>()
                .eq("username", username)
        );
        // 如果用户不存在
        if (user == null) {
            throw new UnknownAccountException();
        }
        // 如果用户存在，验证其密码是否和数据库中一直
        if (!user.getPassword().equals(password)) {
            throw new IncorrectCredentialsException();
        }
        // 设置最后登录时间
        user.setLasted(new Date());

        // 执行更新
        this.updateById(user);

        AccountProfile accountProfile = new AccountProfile();
        // 因为 AccountProfile 中的属性 username，email，created 和 实体类 User 中 的这几个属性是一样的
        // 这里可以直接通过反射拿到
        // source: user   ,  target: accountProfile
        BeanUtils.copyProperties(user, accountProfile);

        return accountProfile;
    }

    @Override
    @Async
    public void sendMail(User user) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject("注册成功");
        message.setText("gokufriday博客社区注册成功，你可以通过用户名"+user.getUsername()+"登录社区");

        message.setTo(user.getEmail());
        message.setFrom("guo1561413067@163.com");

        mailSender.send(message);
    }
}
