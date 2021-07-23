package com.example.chat_app_service.chat_service.repository;


import com.example.chat_app_service.chat_service.repository.entities.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findChatMessagesByChatRoom_IdOrderByTimestampDesc(String roomId, Pageable pageable);
}