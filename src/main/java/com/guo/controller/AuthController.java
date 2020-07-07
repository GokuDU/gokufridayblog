package com.guo.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.code.kaptcha.Producer;
import com.guo.common.lang.Result;
import com.guo.entity.User;
import com.guo.util.ValidationUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class AuthController extends BaseController{

    private static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    @Autowired
    Producer producer;

    @GetMapping("/capthca.jpg")
    public void kaptcha(HttpServletResponse resp) throws IOException {
        // 验证码
        String text = producer.createText();
        // 根据验证码生成图片
        BufferedImage image = producer.createImage(text);
        // 将验证码存入到用户会话
        req.getSession().setAttribute(KAPTCHA_SESSION_KEY, text);

        // 不缓存图片
        resp.setHeader("Cache-Control", "no-store,no-cache");
        // 实体头 让服务器告诉浏览器 发送的数据是图片格式
        resp.setContentType("image/jpeg");
        ServletOutputStream outputStream = resp.getOutputStream();
        ImageIO.write(image, "jpg",outputStream );
    }

    /**
     *  跳转到登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "/auth/login";
    }

    /**
     *  登录
     */
    @ResponseBody
    @PostMapping("/login")
    public Result doLogin(String username,String password) {
        if (StrUtil.isEmpty(username) || StrUtil.isEmpty(password))
            return Result.fail("用户名、密码不能为空");

        // 获取 token 令牌
        UsernamePasswordToken token = new UsernamePasswordToken(username, SecureUtil.md5(password));

        try {
            // getSubject 获取当前用户对象  执行登录操作
            SecurityUtils.getSubject().login(token);

        } catch (AuthenticationException e) {
            if (e instanceof UnknownAccountException) {
                return Result.fail("用户不存在");
            } else if (e instanceof LockedAccountException) {
                return Result.fail("用户被禁用");
            } else if (e instanceof IncorrectCredentialsException) {
                return Result.fail("密码错误");
            } else {
                return Result.fail("用户认证失败");
            }
        }

        return Result.success().action("/");
    }

    /**
     *  跳转到注册页面
     */
    @GetMapping("/register")
    public String register() {
        return "/auth/reg";
    }

    /**
     *  注册
     */
    @ResponseBody
    @PostMapping("/register")
    public Result doRegister(User user,String repass,String vercode) {
        // 校验用户名
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(user);
        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }
        // 校验密码
        if (! user.getPassword().equals(repass)) {
            return Result.fail("两次输入密码不一致");
        }
        // 获取会话中的验证码
        String capthca = (String) req.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        System.out.println(capthca);
        // 校验验证码
        if (vercode == null || !capthca.equals(vercode)) {
            return Result.fail("验证码出错");
        }

        // 完成注册
        Result result = userService.register(user);

        return result.action("/login");
    }

    /**
     *  退出登录
     */
    @RequestMapping("/user/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }

}
