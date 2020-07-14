package com.guo.im.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ImMess {

    private Long id;  // 消息的来源ID（如果是私聊，则是用户id，如果是群聊，则是群组id）
    private String username;
    private String avatar;
    private String type; // 聊天窗口来源类型，从发送消息传递的to里面获取
    private String content;
    private Long cid;
    private Long fromId;
    private Date Timestamp;
    private Boolean mine;

}
