package com.example.chat_app_service.chat_service.service;

import com.example.chat_app_service.chat_service.model.request.ChatMessageRequest;
import com.example.chat_app_service.chat_service.model.request.DeleteMessagaRequest;
import com.example.chat_app_service.chat_service.model.request.ReactMessageRequest;
import com.example.chat_app_service.response.GeneralResponse;
import org.springframework.http.ResponseEntity;

public interface ChatService {
    void processMessage(ChatMessageRequest chatMessageRequest);
    void reactMessage(ReactMessageRequest reactMessageRequest);
    void deleteMessage(DeleteMessagaRequest deleteMessagaRequest);
    ResponseEntity<GeneralResponse<Object>> loadChatMessage(String roomId, int touch);
}
