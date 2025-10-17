package com.Flow.Backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Required!
    private String sender;
    private String content;
    private String receiver; // optional for 1:1 chat
    private MessageType type;
    private LocalDateTime timestamp;


    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}