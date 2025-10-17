package com.Flow.Backend.service;

import com.Flow.Backend.model.ChatMessage;
import com.Flow.Backend.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    public List<ChatMessage> findChats(String senderId,String receiverId){
        return chatRepository.findChats(senderId,receiverId);
    }
}
