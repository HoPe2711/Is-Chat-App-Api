package com.example.chat_app_service.authen_service.model.response;

import static com.example.chat_app_service.authen_service.security.SecurityConstants.TOKEN_PREFIX;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = TOKEN_PREFIX;
    private String id;
    private String username;
    private String displayName;
    private String avatar;
    private String email;

    public LoginResponse( String token, String id, String username, String email, String displayName, String avatar) {
        this.token = token;
        this.id = id;
        this.displayName = displayName;
        this.avatar = avatar;
        this.username =username;
        this.email = email;
    }
}

