package com.example.chat_app_service.chat_service.controller;


import com.example.chat_app_service.chat_service.model.request.ChatMessageRequest;
import com.example.chat_app_service.chat_service.model.request.DeleteMessagaRequest;
import com.example.chat_app_service.chat_service.model.request.ReactMessageRequest;
import com.example.chat_app_service.chat_service.service.ChatService;
import com.example.chat_app_service.response.GeneralResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "*")
@RestController
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageRequest chatMessageRequest) {
        chatService.processMessage(chatMessageRequest);
    }

    @MessageMapping("/react")
    public void reactMessage(@Payload ReactMessageRequest reactMessageRequest) {
        chatService.reactMessage(reactMessageRequest);
    }

    @MessageMapping("/delete_message")
    public void deleteMessage(@Payload DeleteMessagaRequest deleteMessagaRequest) {
        chatService.deleteMessage(deleteMessagaRequest);
    }

    @GetMapping("/chatHistory/{roomId}/{touch}")
    public ResponseEntity<GeneralResponse<Object>> loadChatMessage(@PathVariable String roomId,
                                                                    @PathVariable int touch) {
        return chatService.loadChatMessage(roomId,touch);
    }

    @GetMapping("/findChat/{roomId}/{touch}/{pattern}")
    public ResponseEntity<GeneralResponse<Object>> findChat(@PathVariable String roomId,
        @PathVariable int touch,
        @PathVariable(name="pattern") String pattern) {
        return chatService.findChat(roomId,touch,pattern);
    }
}
