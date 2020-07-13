package com.guo.config;

import com.guo.im.handler.factory.MsgHandlerfactory;
import com.guo.im.server.ImServerStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class ImServerConfig {

    @Value("${im.server.port}")
    private int imPort;

    @Bean
    public ImServerStarter imServerStarter() {
        try {
            // 启动 tio服务
            ImServerStarter imServerStarter = new ImServerStarter(imPort);
            imServerStarter.start();

            // 初始化消息处理器工厂
            MsgHandlerfactory.init();

            log.info("---------> im server started !");
            return imServerStarter;

        } catch (IOException e) {
            log.error("server 启动失败----------》", e);
        }
        // 如果前面异常了
        return null;
    }

}
