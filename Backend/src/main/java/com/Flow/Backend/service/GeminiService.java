package com.Flow.Backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    public  GeminiService(@Value("${gemini.api.key}") String geminiApiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey)
                .build();
    }

    public Mono<String> generateResponse(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", new Object[]{Map.of(
                        "parts", new Object[]{Map.of("text", prompt)}
                )}
        );

        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(resp -> {
                    try {
                        var candidate = ((java.util.List<?>) resp.get("candidates")).get(0);
                        var content = (Map<String, Object>) ((Map<String, Object>) candidate).get("content");
                        var parts = (java.util.List<Map<String, String>>) content.get("parts");
                        return parts.get(0).get("text");
                    } catch (Exception e) {
                        return "Sorry, I couldn’t understand your request.";
                    }
                });
    }
}
