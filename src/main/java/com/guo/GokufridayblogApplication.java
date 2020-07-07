package com.guo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GokufridayblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(GokufridayblogApplication.class, args);
        System.out.println("http://localhost:8080");
    }

}
