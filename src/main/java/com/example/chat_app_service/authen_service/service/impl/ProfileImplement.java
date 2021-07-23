package com.example.chat_app_service.authen_service.service.impl;

import com.example.chat_app_service.authen_service.model.request.ChangeProfile;
import com.example.chat_app_service.authen_service.repository.ApplicationUserRepository;
import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import com.example.chat_app_service.authen_service.service.ProfileService;
import com.example.chat_app_service.response.GeneralResponse;
import com.example.chat_app_service.response.ResponseFactory;
import com.example.chat_app_service.response.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfileImplement implements ProfileService {

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public ProfileImplement(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    private String getUsername(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> getProfile(String userId){
        ApplicationUser applicationUser = applicationUserRepository.findById(userId).orElse(null);
        if (applicationUser == null) return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.NOT_EXIST);
        return ResponseFactory.success(applicationUser);
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> changeProfile(ChangeProfile changeProfile){
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(getUsername());
        applicationUser.setAvatar(changeProfile.getAvatar());
        applicationUser.setDisplayName(changeProfile.getDisplayName());
        applicationUserRepository.save(applicationUser);
        return ResponseFactory.success(applicationUser);
    }
}
