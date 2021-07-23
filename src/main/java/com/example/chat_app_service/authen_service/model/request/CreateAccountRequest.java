package com.example.chat_app_service.authen_service.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateAccountRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String retypePassword;
    @NotBlank
    private  String displayName;
    @NotBlank
    private String avatar;
    @NotBlank
    private  String email;

}
