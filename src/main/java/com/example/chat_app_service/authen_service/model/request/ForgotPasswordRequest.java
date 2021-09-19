package com.example.chat_app_service.authen_service.model.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
}

