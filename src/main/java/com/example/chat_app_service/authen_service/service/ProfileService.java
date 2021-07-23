package com.example.chat_app_service.authen_service.service;

import com.example.chat_app_service.authen_service.model.request.ChangeProfile;
import com.example.chat_app_service.response.GeneralResponse;
import org.springframework.http.ResponseEntity;

public interface ProfileService {
    ResponseEntity<GeneralResponse<Object>> getProfile(String userId);
    ResponseEntity<GeneralResponse<Object>> changeProfile(ChangeProfile changeProfile);
}
