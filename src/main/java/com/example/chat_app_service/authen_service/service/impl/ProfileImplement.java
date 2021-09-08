package com.example.chat_app_service.authen_service.service.impl;

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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class ProfileImplement implements ProfileService {

    private final ApplicationUserRepository applicationUserRepository;
    private static final Path currentFolder = Paths.get(System.getProperty("user.dir"));

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
    public ResponseEntity<GeneralResponse<Object>> changeProfile(MultipartFile multipartFile, String displayName) throws IOException {
        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("images");
        if (!Files.exists(currentFolder.resolve(staticPath).resolve(imagePath))) {
            Files.createDirectories(currentFolder.resolve(staticPath).resolve(imagePath));
        }

        ApplicationUser applicationUser = applicationUserRepository.findByUsername(getUsername());
        Path oldfile = currentFolder.resolve(staticPath)
                .resolve(imagePath).resolve(applicationUser.getAvatar());
        oldfile.toFile().delete();

        String pathAva = getUsername() + multipartFile.getOriginalFilename().substring(
                multipartFile.getOriginalFilename().lastIndexOf(".")
        );
        Path file = currentFolder.resolve(staticPath)
                .resolve(imagePath).resolve(pathAva);
        try (OutputStream os = Files.newOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }

        applicationUser.setAvatar(pathAva);
        applicationUser.setDisplayName(displayName);
        applicationUserRepository.save(applicationUser);
        return ResponseFactory.success(applicationUser);
    }
}
