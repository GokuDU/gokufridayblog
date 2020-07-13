package com.guo.im.server;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.guo.im.handler.MsgHandler;
import com.guo.im.handler.factory.MsgHandlerfactory;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.util.Map;

@Slf4j
public class ImWsMsgHandler implements IWsMsgHandler {

    /**
     *  握手时的方法
     *      返回 HttpResponse  支持握手
     *      返回 null         不支持握手
     * @param httpRequest
     * @param httpResponse
     * @param channelContext
     * @return
     * @throws Exception
     */
    @Override
    public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        return httpResponse;
    }

    /**
     *  握手完成之后
     * @param httpRequest
     * @param httpResponse
     * @param channelContext
     * @throws Exception
     */
    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {

    }

    /**
     *  接收字节类型消息
     * @param wsRequest
     * @param bytes
     * @param channelContext
     * @return
     * @throws Exception
     */
    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }

    /**
     *  接收字符类型消息
     * @param wsRequest
     * @param text  type,data
     * @param channelContext
     * @return
     * @throws Exception
     */
    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception {

        log.info("接收到消息——————————————{}>",text);

        // 从工厂里通过 type(类型) 获取对应的 handler(处理器)
        Map map = JSONUtil.toBean(text, Map.class);

        String type = MapUtil.getStr(map, "type");
        String data = MapUtil.getStr(map, "data");

        MsgHandler msgHandler = MsgHandlerfactory.getMsgHandler(type);
        // 处理消息

        return null;
    }

    /**
     *  连接关闭时的方法
     * @param wsRequest
     * @param bytes
     * @param channelContext
     * @return
     * @throws Exception
     */
    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }
}
