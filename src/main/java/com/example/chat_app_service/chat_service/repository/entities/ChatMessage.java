package com.example.chat_app_service.chat_service.repository.entities;

import com.example.chat_app_service.chat_service.model.MessageStatus;
import com.example.chat_app_service.chat_service.model.request.ChatMessageRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 5030309224378679309L;
    @Id
    private String id;
    private String senderId;
    private String senderName;
    @DBRef(lazy = true)
    @JsonIgnore
    private ChatRoom chatRoom;
    private String chatRoomId;
    private String chatRoomName;
    private String content;
    private Long timestamp;
    private MessageStatus status;

    public ChatMessage(ChatMessageRequest chatMessageRequest, ChatRoom chatRoom){
        this.senderId = chatMessageRequest.getSenderId();
        this.senderName = chatMessageRequest.getSenderName();
        this.content = chatMessageRequest.getContent();
        this.timestamp = new Date().getTime();
        this.chatRoomName = chatRoom.getRoomName();
        this.chatRoomId = chatRoom.getId();
        this.chatRoom = chatRoom;
    }
}