package com.example.chat_app_service.authen_service.service;

import com.example.chat_app_service.authen_service.model.request.ChangePasswordRequest;
import com.example.chat_app_service.authen_service.model.request.CreateAccountRequest;
import com.example.chat_app_service.authen_service.model.request.ForgotPasswordRequest;
import com.example.chat_app_service.authen_service.model.request.LoginRequest;
import com.example.chat_app_service.authen_service.model.request.ResetPasswordRequest;
import com.example.chat_app_service.authen_service.model.request.VerifyAccountRequest;
import com.example.chat_app_service.response.GeneralResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

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
