package com.Flow.Backend.service;

import com.Flow.Backend.model.Chat;
import com.Flow.Backend.model.CommunityModel;
import com.Flow.Backend.model.Message;
import com.Flow.Backend.model.PostModel;
import com.Flow.Backend.repository.ChatRepository;
import com.Flow.Backend.repository.CommunityRepository;
import com.Flow.Backend.repository.MessageRepository;
import com.Flow.Backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class ChatService {
    @Autowired
    private GeminiService geminiService;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;

    public Mono<String> processMessage(String message) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Chat chat = chatRepository.findByUsername(username).orElseGet(() -> {
            Chat newChat = new Chat();
            newChat.setUsername(username);
            return chatRepository.save(newChat);
        });

        // Fetch all relevant data from DB
        List<CommunityModel> communities = communityRepository.findAll();
        List<PostModel> posts = postRepository.findAll();

        String communityData = communities.stream()
                .map(c -> "Community: " + c.getName() + " - " + c.getDescription())
                .collect(Collectors.joining("\n"));

        String postData = posts.stream()
                .map(p -> "Post: " + p.getTitle() + " by " + p.getCreatedByUser())
                .collect(Collectors.joining("\n"));

        String previous = chat.getMessages().stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        String prompt = """
            You are Flow AI, a chatbot assistant that helps users explore their communities and posts.

            ---COMMUNITY DATA---
            %s

            ---POST DATA---
            %s

            ---CHAT HISTORY---
            %s

            User: %s
            """.formatted(communityData, postData, previous, message);

        // Save user message
        Message userMsg = new Message();
        userMsg.setChat(chat);
        userMsg.setRole("user");
        userMsg.setContent(message);
        messageRepository.save(userMsg);

        // Get AI response
        return geminiService.generateResponse(prompt)
                .flatMap(reply -> {
                    Message aiMsg = new Message();
                    aiMsg.setChat(chat);
                    aiMsg.setRole("assistant");
                    aiMsg.setContent(reply);
                    messageRepository.save(aiMsg);
                    return Mono.just(reply);
                });
    }
}
