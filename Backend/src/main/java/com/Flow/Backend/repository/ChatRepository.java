package com.Flow.Backend.repository;

import com.Flow.Backend.model.Chat;
import com.Flow.Backend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    @Query(value = "SELECT * FROM chat_message WHERE (sender = :senderId AND receiver = :receiverId) OR (sender = :receiverId AND receiver = :senderId)", nativeQuery = true)
    List<ChatMessage> findChats(@Param("senderId") String senderId, @Param("receiverId") String receiverId);
}