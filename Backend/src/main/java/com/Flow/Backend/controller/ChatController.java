package com.Flow.Backend.controller;

import com.Flow.Backend.model.ChatMessage;
import com.Flow.Backend.repository.ChatRepository;
import com.Flow.Backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chat")

public class ChatController {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatService chatService;

    // Client sends to /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage message) {

        ChatMessage chat=new ChatMessage();
        chat.setContent(message.getContent());
        chat.setSender(message.getSender());
        chat.setTimestamp(LocalDateTime.now());
        chat.setType(message.getType());
        chat.setReceiver(message.getReceiver());
        ChatMessage savedChat = chatRepository.save(chat);


        return savedChat;
    }

    // Optional: handle join messages
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage message) {
        message.setType(ChatMessage.MessageType.JOIN);
        return message;
    }
    @GetMapping("/get/{receiverId}")
    public List<ChatMessage> getMessage(@PathVariable String receiverId, @RequestParam String sender){

        return chatService.findChats(sender,receiverId);
    }
}