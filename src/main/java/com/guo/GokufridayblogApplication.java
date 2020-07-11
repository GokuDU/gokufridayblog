package com.guo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GokufridayblogApplication {

    public static void main(String[] args) {

        // 解决es启动保存问题  设置不检查 netty 版本
        System.setProperty("es.set.netty.runtime.available.processors", "false");

        SpringApplication.run(GokufridayblogApplication.class, args);
        System.out.println("http://localhost:8080");
    }

}
