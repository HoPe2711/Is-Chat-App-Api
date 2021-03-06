package com.example.chat_app_service.authen_service.controller;

import com.example.chat_app_service.authen_service.model.request.CreateAccountRequest;
import com.example.chat_app_service.authen_service.model.request.ForgotPasswordRequest;
import com.example.chat_app_service.authen_service.model.request.LoginRequest;
import com.example.chat_app_service.authen_service.model.request.ResetPasswordRequest;
import com.example.chat_app_service.authen_service.model.request.VerifyAccountRequest;
import com.example.chat_app_service.authen_service.service.UserService;
import com.example.chat_app_service.response.GeneralResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "*")
@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<GeneralResponse<Object>> signUp(@RequestBody CreateAccountRequest createAccountRequest, HttpServletRequest request){
        return userService.signupAccount(createAccountRequest,request);
    }

    @PostMapping("/verify")
    public ResponseEntity<GeneralResponse<Object>> verify(@RequestBody VerifyAccountRequest verifyAccountRequest){
        return userService.verifySignUp(verifyAccountRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<Object>> login(@Valid @RequestBody LoginRequest loginRequest){
        return userService.loginAccount(loginRequest);
    }

    @PostMapping("/forgot")
    public ResponseEntity<GeneralResponse<Object>> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest httpServletRequest){
        return userService.forgotPassword(forgotPasswordRequest,httpServletRequest);
    }

    @PostMapping("/reset")
    public ResponseEntity<GeneralResponse<Object>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        return userService.resetPassword(resetPasswordRequest);
    }
}