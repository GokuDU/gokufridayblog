package com.guo.im.handler.message;

import com.guo.im.vo.ImMess;
import com.guo.im.vo.ImUser;
import lombok.Data;

@Data
public class ChatOutMess {

    private String emit;
    private ImMess data;

}
