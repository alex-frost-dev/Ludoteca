package com.ccsw.tutorialauthor.game;

import com.ccsw.tutorialauthor.game.model.GameDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "SPRING-CLOUD-EUREKA-CLIENT-GAME", url = "http://localhost:8080")
public interface GameClient {

    @GetMapping(value = "/game")
    List<GameDto> findAll();
}
