package com.guo.common.lang;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class Consts {

    @Value("${file.upload.dir}")
    private String uploadDir;

    public final static String LIKE_COMMENT = "已点赞过";

    public final static Long IM_DEFAULT_USER_ID = 9999L;

    // 群聊Id
    public final static Long IM_GROUP_ID = 9999L;
    public final static String IM_GROUP_NAME = "gokufriday-blog-group";

    // MsgHandler 的 type  消息类型
    public final static String IM_MESS_TYPE_PING = "pingMessage";
    public final static String IM_MESS_TYPE_CHAT = "chatMessage";

    public final static String IM_ONLINE_MEMBERS_KEY = "online-members-key";
    public final static String IM_GROUP_HISTORY_MSG_KEY = "group-histroy-msg-key";

}
