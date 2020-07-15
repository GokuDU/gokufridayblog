package com.guo.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.guo.common.lang.Consts;
import com.guo.im.vo.ImMess;
import com.guo.im.vo.ImUser;
import com.guo.service.ChatService;
import com.guo.shiro.AccountProfile;
import com.guo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service("chatService")
public class ChatServiceImpl implements ChatService {

    @Autowired
    RedisUtil redisUtil;

    // 获取当前聊天用户
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
            // https://c-ssl.duitang.com/uploads/item/201705/07/20170507160547_FQcvE.thumb.1000_0.jpeg
            // http://tp1.sinaimg.cn/5619439268/180/40030060651/1
            imUser.setAvatar("http://tp1.sinaimg.cn/5619439268/180/40030060651/1");

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

    // 设置聊天历史记录
    @Override
    public void setGroupHistoryMsg(ImMess imMess) {
        redisUtil.lSet(Consts.IM_GROUP_HISTORY_MSG_KEY, imMess, 15*24*60*60);
    }

    // 获取聊天历史记录
    @Override
    public List<Object> getGroupHistoryMsg(int count) {
        // 获取历史数据总长度
        long length = redisUtil.lGetListSize(Consts.IM_GROUP_HISTORY_MSG_KEY);
        // 获取历史数据
        // lGet(String key, long start, long end)
        // length 总数据长度 - count 要显示的数据长度
        //     展示数据范围：
        //          length < count ==> 0 ~ length
        //          length > count ==> length-count ~ length
        return redisUtil.lGet(Consts.IM_GROUP_HISTORY_MSG_KEY,length - count < 0 ? 0 : length-count ,length);
    }
}
