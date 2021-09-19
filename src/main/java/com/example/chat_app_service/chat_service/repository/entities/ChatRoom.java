package com.example.chat_app_service.chat_service.repository.entities;

import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import com.example.chat_app_service.chat_service.model.request.CreateRoomRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatRoom implements Serializable {
    private static final long serialVersionUID = -7314556937210235830L;
    @Id
    private String id;
    private String roomName;
    @DBRef(lazy = true)
    @JsonIgnoreProperties(value = "chatRoomList")
    private Set<ApplicationUser> userList  = new HashSet<>();

    @JsonIgnoreProperties(value = "chatRoomList")
    private ApplicationUser host;

    private String content;
    private String senderName;
    private Long timestamp;

    public ChatRoom(CreateRoomRequest createRoomRequest, ApplicationUser applicationUser){
        this.roomName = createRoomRequest.getRoomName();
        this.content = "Now you can chat with your friends";
        this.timestamp = new Date().getTime();
        this.senderName = "iMess";
        this.host = applicationUser;
        this.userList.add(applicationUser);
    }

    public void setChatRoomByChatMessage(ChatMessage chatMessage){
        this.content = chatMessage.getContent();
        this.senderName = chatMessage.getSenderName();
        this.timestamp = chatMessage.getTimestamp();
    }
}