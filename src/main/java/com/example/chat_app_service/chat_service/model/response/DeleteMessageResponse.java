package com.example.chat_app_service.chat_service.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMessageResponse {
    private String senderId;
    private String senderName;
    private String chatId;
    private String chatRoomId;
    private String chatRoomName;
    private String delete;
}
