package com.example.chat_app_service.authen_service.service;

import com.example.chat_app_service.response.GeneralResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    ResponseEntity<GeneralResponse<Object>> getProfile(String userId);
    ResponseEntity<GeneralResponse<Object>> changeProfile(MultipartFile multipartFile, String displayName) throws IOException;
}
