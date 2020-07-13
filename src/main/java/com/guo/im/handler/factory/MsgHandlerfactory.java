package com.guo.im.handler.factory;

import com.guo.common.lang.Consts;
import com.guo.im.handler.MsgHandler;
import com.guo.im.handler.impl.ChatMsgHandler;
import com.guo.im.handler.impl.PingMsgHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 *  消息处理器工厂
 */
@Slf4j
public class MsgHandlerfactory {

    private static Map<String,MsgHandler> handlerMap = new HashMap<>();

    public static void init() {
        handlerMap.put(Consts.IM_MESS_TYPE_CHAT, new ChatMsgHandler());
        handlerMap.put(Consts.IM_MESS_TYPE_PING, new PingMsgHandler());

        log.info("handler factory init------------------->>>");
    }

    public static MsgHandler getMsgHandler(String type) {
        return handlerMap.get(type);
    }

}
