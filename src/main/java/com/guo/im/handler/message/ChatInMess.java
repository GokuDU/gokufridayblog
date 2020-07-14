package com.guo.im.handler.message;

import com.guo.im.vo.ImTo;
import com.guo.im.vo.ImUser;
import lombok.Data;

@Data
public class ChatInMess {
    private ImUser mine;
    private ImTo to;
}
