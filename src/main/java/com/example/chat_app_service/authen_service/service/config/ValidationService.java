package com.example.chat_app_service.authen_service.service.config;

import com.example.chat_app_service.authen_service.repository.ApplicationUserRepository;
import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import com.example.chat_app_service.response.GeneralResponse;
import com.example.chat_app_service.response.ResponseFactory;
import com.example.chat_app_service.response.ResponseStatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    private final ApplicationUserRepository applicationUserRepository;

    public ValidationService(ApplicationUserRepository applicationUserRepository){
        this.applicationUserRepository = applicationUserRepository;
    }

    private boolean findUsername(String username){
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        return applicationUser != null;
    }

    private long findEmail(String email){
        ApplicationUser applicationUser = applicationUserRepository.findByEmail(email);
        if (applicationUser != null)
            if (applicationUser.isStatus()) return 2;
            else return 1;
        return 0;
    }

    private boolean checkCharacter(char character) {
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z');
    }

    private boolean checkNum(char num) {
        return num >= '0' && num <= '9';
    }

    private boolean formatUsernameAndPassword(String username) {
        int length = username.length();
        if (length < 8 || length > 50) return false;
        for (int i = 0; i < length; i++) {
            char character = username.charAt(i);
            if (!checkCharacter(character) && !checkNum(character)) return false;
        }
        return true;
    }


    private boolean checkEmailPart(String emailPart) {
        char character = emailPart.charAt(0);
        int pre = -1;
        if (!checkCharacter(character) && !checkNum(character)) return false;
        character = emailPart.charAt(emailPart.length() - 1);
        if (!checkCharacter(character) && !checkNum(character)) return false;
        int length = emailPart.length();
        for (int i = 0; i < length; i++) {
            character = emailPart.charAt(i);
            if (!checkCharacter(character) && !checkNum(character) && character != '.') return false;
            if (character == '.') {
                if (i == pre + 1) return false;
                pre = i;
            }
        }
        return true;
    }

    private boolean formatEmail(String email) {
        if (email.length() > 100 || email.length() < 10) return false;
        int pos = email.indexOf('@');
        if (pos < 1) return false;
        if (pos == email.length() - 1) return false;
        if (!checkEmailPart(email.substring(0, pos))) return false;
        String emailPart = email.substring(pos + 1);
        if (emailPart.indexOf('.') == -1) return false;
        return checkEmailPart(emailPart);
    }

    public boolean checkRetypePassword(String retypePassword, String password) {
        return retypePassword.equals(password);
    }

    public ResponseEntity<GeneralResponse<Object>> validRequest(String username,
                                                                String password,
                                                                String email) {
        if (!formatUsernameAndPassword(username))
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.USERNAME_ERROR);
        if (!formatUsernameAndPassword(password))
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.PASSWORD_ERROR);
        if (!formatEmail(email))
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.EMAIL_ERROR);
        return validExist(username, email);
    }

    public ResponseEntity<GeneralResponse<Object>> validExist(String username, String email){
        if (findUsername(username))
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.USER_EXIST);

        long status = findEmail(email);
        if (status==2)
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.EMAIL_EXIST);
        else if (status == 1)
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.VERIFIED_EMAIL);
        return null;
    }

    public ResponseEntity<GeneralResponse<Object>> validateChangePassword(String newPassword, String retypeNewPassword) {
        if (!formatUsernameAndPassword(newPassword))
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.PASSWORD_ERROR);
        if (!checkRetypePassword(retypeNewPassword,newPassword))
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.RETYPE_ERROR);
        return null;
    }
}