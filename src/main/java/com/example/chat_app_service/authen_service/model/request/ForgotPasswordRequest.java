package com.example.chat_app_service.authen_service.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ForgotPasswordRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
}

