package com.example.chat_app_service.chat_service.repository;


import com.example.chat_app_service.chat_service.repository.entities.ChatMessage;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findChatMessagesByChatRoom_IdOrderByTimestampDesc(String roomId, Pageable pageable);
}