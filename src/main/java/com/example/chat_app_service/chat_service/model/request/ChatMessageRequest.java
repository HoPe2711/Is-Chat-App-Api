package com.example.chat_app_service.chat_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
    private String senderId;
    private String senderName;
    private String chatRoomId;
    private String content;
}
