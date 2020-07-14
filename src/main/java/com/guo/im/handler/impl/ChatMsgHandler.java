package com.guo.im.handler.impl;

import cn.hutool.json.JSONUtil;
import com.guo.common.lang.Consts;
import com.guo.im.handler.MsgHandler;
import com.guo.im.handler.filter.LimitMyChannelContextFilter;
import com.guo.im.handler.message.ChatInMess;
import com.guo.im.handler.message.ChatOutMess;
import com.guo.im.vo.ImMess;
import com.guo.im.vo.ImTo;
import com.guo.im.vo.ImUser;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;

import java.util.Date;

@Slf4j
public class ChatMsgHandler implements MsgHandler {
    @Override
    public void handler(String data, WsRequest wsRequest, ChannelContext channelContext) {
        // 处理 data 解析
        ChatInMess chatInMess = JSONUtil.toBean(data, ChatInMess.class);

        // 监听发送消息
        ImUser imUser = chatInMess.getMine();
        ImTo imTo = chatInMess.getTo();

        log.info("user====================》》》》》"+imUser.getUsername());

        // 如果是来自于用户的聊天消息，它必须接受以下字段
        ImMess imMess = new ImMess();
        imMess.setContent(imUser.getContent());
        imMess.setAvatar(imUser.getAvatar());
        imMess.setMine(false); // 是否我发送的消息，如果为true，则会显示在右方(我发送的消息，对于其他用户来说为 false)
        imMess.setUsername(imUser.getUsername());
        imMess.setFromId(imUser.getId());
        imMess.setId(Consts.IM_GROUP_ID); // 如果是群聊，则是群组id
        imMess.setTimestamp(new Date());
        imMess.setType(imTo.getType());  // //聊天窗口来源类型，从发送消息传递的to里面获取

        log.info("imMess.getUsername=====>user=========》》》》》"+imMess.getUsername());

        // 监听接收消息
        ChatOutMess chatOutMess = new ChatOutMess();
        chatOutMess.setEmit("chatMessage");
        chatOutMess.setData(imMess);

        // 对象序列化为 json字符串
        String jsonResult = JSONUtil.toJsonStr(chatOutMess);
        log.info("群聊消息---------》"+ jsonResult);

        // WsResponse ---> extends WsPacket ---> extends Packet
        WsResponse wsResponse = WsResponse.fromText(jsonResult,"utf-8" );

        LimitMyChannelContextFilter channelContextFilter = new LimitMyChannelContextFilter();
        channelContextFilter.setCurrentContext(channelContext);

        Tio.sendToGroup(channelContext.getGroupContext(), Consts.IM_GROUP_NAME, wsResponse,channelContextFilter);
    }
}
