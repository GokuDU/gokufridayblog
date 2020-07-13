package com.guo.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.guo.im.vo.ImUser;
import com.guo.service.ChatService;
import com.guo.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ChatServiceImpl implements ChatService {

    @Override
    public ImUser getCurrentUser() {

        // 获取当前用户对象 的 数据
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        ImUser imUser = new ImUser();

        if (accountProfile != null) {
            // 登录用户
            imUser.setId(accountProfile.getId());
            imUser.setAvatar(accountProfile.getAvatar());
            imUser.setUsername(accountProfile.getUsername());
            imUser.setStatus(ImUser.ONLINE_STATUS);

        } else {
            // 匿名用户
            imUser.setAvatar("https://c-ssl.duitang.com/uploads/item/201705/07/20170507160547_FQcvE.thumb.1000_0.jpeg");

            // 先从会话里获取 id（imUserID）
            // 如果 imUserID 为空 ，则set进去一个随机值
            Long imUserId = (Long) SecurityUtils.getSubject().getSession().getAttribute("imUserId");
            imUser.setId(imUserId != null ? imUserId : RandomUtil.randomLong());
            // 确保会话是同一个身份，防止登录后会话身份改变
            SecurityUtils.getSubject().getSession().setAttribute("imUserId", imUser.getId());

            imUser.setSign("I'm your friend");
            imUser.setUsername("匿名用户");
            imUser.setStatus(ImUser.HIDE_STATUS);

        }

        return imUser;
    }
}
