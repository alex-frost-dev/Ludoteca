package com.ccsw.tutorialgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TutorialGame {
    public static void main(String[] args) {
        SpringApplication.run(TutorialGame.class, args);
    }
}