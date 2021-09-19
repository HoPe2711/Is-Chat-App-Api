package com.example.chat_app_service.authen_service.model.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@NotBlank
public class VerifyAccountRequest {
    private String token;
}
