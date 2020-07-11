package com.guo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guo.entity.UserMessage;
import com.guo.service.UserMessageService;
import com.guo.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    UserMessageService userMessageService;

    // 注入 WebSocket 操作模板
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    // 即使发送消息给 接收消息的人
    // 异步操作
    @Async
    @Override
    public void sendInstantMessageCountToUser(Long toUserId) {
        // 未读消息
        int count = userMessageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", toUserId)
                .eq("status", "0")
        );

        // webSocket 发送通知
        // 浏览器控制台的打印的发送通道   destination:/user/8/messCount
        simpMessagingTemplate.convertAndSendToUser(toUserId.toString(), "/messCount", count);

    }
}
