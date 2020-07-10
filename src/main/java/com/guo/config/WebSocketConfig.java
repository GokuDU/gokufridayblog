package com.guo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // 开启使用 STOMP协议 来传输基于代理的消息
@EnableAsync    // 启动异步
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 注册端点
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/webSocket")  // 注册一个端点， "/webSocket"代表 webSocket 的访问地址
                .withSockJS();  // withSockJS 当浏览器不支持webSocket，会采用一些降级的技术
    }

    // 配置消息代理点
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/user/","/topic/" 推送消息前缀
        //  /user/   点对点推送
        //  /topic/  发布、订阅推送
        registry.enableSimpleBroker("/user/","/topic/");
        // 注册Application代理点  使用 "/app" 开头  就会进入代理点
        registry.setApplicationDestinationPrefixes("/app");
    }
}
