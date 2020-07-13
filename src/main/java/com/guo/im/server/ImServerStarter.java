package com.guo.im.server;

import lombok.extern.slf4j.Slf4j;
import org.tio.server.ServerGroupContext;
import org.tio.websocket.server.WsServerStarter;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.IOException;

/**
 *  启动 tio
 */
@Slf4j
public class ImServerStarter {

    private WsServerStarter wsServerStarter;

    public ImServerStarter(int imPort) throws IOException {

        IWsMsgHandler handler = new ImWsMsgHandler();
        // WsServerStarter(int port, IWsMsgHandler wsMsgHandler)  port 端口 、 wsMsgHandler 消息处理器
        wsServerStarter = new WsServerStarter(imPort,handler);

        // 心跳机制
        // 通过 配置上下文 设置心跳
        ServerGroupContext serverGroupContext = wsServerStarter.getServerGroupContext();
        serverGroupContext.setHeartbeatTimeout(1000*60);
    }

    public void start() throws IOException {
        wsServerStarter.start();
        log.info("tio server start------------->>");
    }

}
