package com.example.chat_app_service.authen_service.model.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeProfile {
    @NotBlank
    private String displayName;
}
