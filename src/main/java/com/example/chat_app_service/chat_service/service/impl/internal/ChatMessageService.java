package com.example.chat_app_service.chat_service.service.impl.internal;

import com.example.chat_app_service.chat_service.model.MessageStatus;
import com.example.chat_app_service.chat_service.repository.ChatMessageRepository;
import com.example.chat_app_service.chat_service.repository.entities.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage save(ChatMessage chatMessage){
        chatMessage.setStatus(MessageStatus.RECEIVED);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

}
