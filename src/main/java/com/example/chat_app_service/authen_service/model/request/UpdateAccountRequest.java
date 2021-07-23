package com.example.chat_app_service.authen_service.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@NotBlank
public class UpdateAccountRequest {
    private String displayName;
    private String avatar;
}
