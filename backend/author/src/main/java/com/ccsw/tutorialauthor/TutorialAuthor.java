package com.ccsw.tutorialauthor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TutorialAuthor {
    public static void main(String[] args) {
        SpringApplication.run(TutorialAuthor.class, args);
    }
}