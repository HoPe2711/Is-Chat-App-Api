package com.example.chat_app_service.authen_service.controller;

import com.example.chat_app_service.authen_service.model.request.ChangePasswordRequest;
import com.example.chat_app_service.authen_service.service.ProfileService;
import com.example.chat_app_service.authen_service.service.UserService;
import com.example.chat_app_service.response.GeneralResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(value = "*")
@Slf4j
@RestController
@RequestMapping("/auth")
public class UserAuthController {

    private final UserService userService;
    private final ProfileService profileService;

    @Autowired
    public UserAuthController(UserService userService, ProfileService profileService){
        this.userService = userService;
        this.profileService = profileService;
    }

    @PostMapping("/changepassword")
    public ResponseEntity<GeneralResponse<Object>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        return userService.changePassword(changePasswordRequest);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<GeneralResponse<Object>> getProfile(@PathVariable(name="userId") String userId){
        return profileService.getProfile(userId);
    }

    @PutMapping(value = "/profile")
    public ResponseEntity<GeneralResponse<Object>> changeProfile(@RequestParam("image")MultipartFile multipartFile, @RequestParam("displayName") String displayName) throws IOException {
        return profileService.changeProfile(multipartFile, displayName);
    }

    @GetMapping("/find_user/{pattern}/{touch}")
    public ResponseEntity<GeneralResponse<Object>> findUser(@PathVariable(name="pattern") String pattern,
                                                            @PathVariable(name="touch") int touch){
        return userService.findUser(pattern,touch);
    }


    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse<Object>> logout(){
        return userService.logoutAccount();
    }

}