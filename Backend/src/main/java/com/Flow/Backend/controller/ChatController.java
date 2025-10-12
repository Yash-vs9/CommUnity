package com.Flow.Backend.controller;

import com.Flow.Backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/input")
    public Mono<Map<String ,String>> chat(@RequestBody Map<String ,String> body){
        String message=body.get("message");
        return chatService.processMessage(message)
                .map(reply->Map.of("reply",reply));
    }
}
