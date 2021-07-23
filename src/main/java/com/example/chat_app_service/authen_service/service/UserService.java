package com.example.chat_app_service.authen_service.service;

import com.example.chat_app_service.authen_service.model.request.*;
import com.example.chat_app_service.response.GeneralResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    ResponseEntity<GeneralResponse<Object>> loginAccount(LoginRequest loginRequest);
    ResponseEntity<GeneralResponse<Object>> logoutAccount();
    ResponseEntity<GeneralResponse<Object>> verifySignUp(VerifyAccountRequest verifyAccountRequest);
    ResponseEntity<GeneralResponse<Object>> changePassword(ChangePasswordRequest changePasswordRequest);
    ResponseEntity<GeneralResponse<Object>> signupAccount(CreateAccountRequest createAccountRequest, HttpServletRequest request);
    ResponseEntity<GeneralResponse<Object>> forgotPassword(ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest httpServletRequest);
    ResponseEntity<GeneralResponse<Object>> resetPassword(ResetPasswordRequest resetPasswordRequest);
    ResponseEntity<GeneralResponse<Object>> findUser(String patternName, int touch);
}
