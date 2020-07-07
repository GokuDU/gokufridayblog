package com.guo.vo;

import com.guo.entity.UserMessage;
import lombok.Data;

@Data
public class UserMessageVO extends UserMessage {

    private String toUserName;
    private String fromUserName;
    private String postTitle;
    private String commentContent;

}
