package com.example.chat_app_service.authen_service.repository.entities;

import com.example.chat_app_service.authen_service.model.request.CreateAccountRequest;
import com.example.chat_app_service.chat_service.repository.entities.ChatRoom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ApplicationUser implements Serializable {
    private static final long serialVersionUID = 885755982832759979L;
    @Id
    private String id;
    private String username;
    @JsonIgnore
    private String password;
    private String displayName;
    private Binary avatar;
    private String email;
    private boolean status;

    @DBRef(lazy = true)
    @JsonIgnore//Properties(value = "userList")
    private Set<ChatRoom> chatRoomList = new HashSet<>();

    public void setCreateAccountRequest(CreateAccountRequest createAccountRequest) {
        this.username = createAccountRequest.getUsername();
        this.password = createAccountRequest.getPassword();
        this.displayName = createAccountRequest.getDisplayName();
        this.email = createAccountRequest.getEmail();
        this.status = false;
    }
}
