package com.example.chat_app_service.chat_service.repository.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class React {
    private String senderId;
    private String senderName;
    private String emotion;
}
