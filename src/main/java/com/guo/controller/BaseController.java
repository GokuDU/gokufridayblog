package com.guo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.UserMessage;
import com.guo.service.*;
import com.guo.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Autowired
    HttpServletRequest req;

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    UserMessageService userMessageService;

    @Autowired
    UserCollectionService userCollectionService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    SearchService searchService;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    UserActionService userActionService;

    @Autowired
    ChatService chatService;


    public Page getPage() {
        // request.getParameter(name) == null   return defaultVal
        // 当前页 默认为 1  页面大小 默认为 3
        int pn = ServletRequestUtils.getIntParameter(req, "pn",1);
        int size = ServletRequestUtils.getIntParameter(req, "size",3);
        return new Page(pn,size);
    }

    public Page getPagePlus() {
        // request.getParameter(name) == null   return defaultVal
        // 当前页 默认为 1  页面大小 默认为 5
        int pn = ServletRequestUtils.getIntParameter(req, "pn",1);
        int size = ServletRequestUtils.getIntParameter(req, "size",5);
        return new Page(pn,size);
    }

    // 获取当前用户对象 的 数据
    protected AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    // 获取 id
    protected Long getProfileId() {
        return getProfile().getId();
    }


}
