package com.example.chat_app_service.authen_service.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangeProfile {
    @NotBlank
    private String displayName;
    @NotBlank
    private String avatar;
}