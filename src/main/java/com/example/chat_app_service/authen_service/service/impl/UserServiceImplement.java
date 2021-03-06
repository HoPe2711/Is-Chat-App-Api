package com.example.chat_app_service.authen_service.service.impl;

import com.example.chat_app_service.authen_service.mailsender.EmailService;
import com.example.chat_app_service.authen_service.model.request.ChangePasswordRequest;
import com.example.chat_app_service.authen_service.model.request.CreateAccountRequest;
import com.example.chat_app_service.authen_service.model.request.ForgotPasswordRequest;
import com.example.chat_app_service.authen_service.model.request.LoginRequest;
import com.example.chat_app_service.authen_service.model.request.ResetPasswordRequest;
import com.example.chat_app_service.authen_service.model.request.VerifyAccountRequest;
import com.example.chat_app_service.authen_service.model.response.LoginResponse;
import com.example.chat_app_service.authen_service.redis.service.IRedisCaching;
import com.example.chat_app_service.authen_service.repository.ApplicationUserRepository;
import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import com.example.chat_app_service.authen_service.security.filter.JWT.JwtUtils;
import com.example.chat_app_service.authen_service.security.filter.service.UserDetailsImplement;
import com.example.chat_app_service.authen_service.service.UserService;
import com.example.chat_app_service.authen_service.service.config.ValidationService;
import com.example.chat_app_service.response.GeneralResponse;
import com.example.chat_app_service.response.ResponseFactory;
import com.example.chat_app_service.response.ResponseStatusEnum;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImplement implements UserService {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${token.ttl}")
    private Long ttl;

    private final String defaultAva = "defaultAva.jpg";

    private final ApplicationUserRepository applicationUserRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ValidationService validationService;

    private final IRedisCaching iRedisCaching;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final EmailService emailService;

    @Autowired
    public UserServiceImplement(ApplicationUserRepository applicationUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ValidationService validationService, IRedisCaching iRedisCaching, AuthenticationManager authenticationManager, JwtUtils jwtUtils, EmailService emailService) {
        this.applicationUserRepository = applicationUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.validationService = validationService;
        this.iRedisCaching = iRedisCaching;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    private String addAccount(CreateAccountRequest createAccountRequest){
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setCreateAccountRequest(createAccountRequest);
        applicationUserRepository.save(applicationUser);
        return applicationUser.getId();
    }

    private ResponseEntity<GeneralResponse<Object>> checkAccount(LoginRequest loginRequest) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(loginRequest.getUsername());
        if (applicationUser == null)
            return ResponseFactory.error(HttpStatus.valueOf(403), ResponseStatusEnum.NOT_EXIST);
        if (!applicationUser.isStatus())
            return ResponseFactory.error(HttpStatus.valueOf(403), ResponseStatusEnum.VERIFIED_EMAIL);
        if (applicationUser.isStatus() && new BCryptPasswordEncoder().matches(loginRequest.getPassword(), applicationUser.getPassword()))
            return null;
        return ResponseFactory.error(HttpStatus.valueOf(403), ResponseStatusEnum.WRONG_USERNAME_OR_PASSWORD);
    }

    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        return String.format("%06d", number);
    }

    private void sendMail(String mail, String token) {
        CompletableFuture.runAsync(() ->
                emailService.sendEmail(username,
                        mail,
                        "Password Reset Request",
                        "Type this verify code on our app:\n" + token)
        );
        log.info("Mail has been sent!");
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> changePassword(ChangePasswordRequest changePasswordRequest) {
        ResponseEntity<GeneralResponse<Object>> validateResult = validationService.validateChangePassword(changePasswordRequest.getNewPassword(), changePasswordRequest.getRetypePassword());
        if (validateResult != null) return validateResult;
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        if (new BCryptPasswordEncoder().matches(changePasswordRequest.getOldPassword(), applicationUser.getPassword())) {
            applicationUser.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword()));
            applicationUserRepository.save(applicationUser);
            return ResponseFactory.success("Password has changed !");
        }
        return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.RETYPE_OLD_PASSWORD_ERROR);
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> loginAccount(LoginRequest loginRequest) {
        ResponseEntity<GeneralResponse<Object>> loginResult = checkAccount(loginRequest);
        if (loginResult != null) return loginResult;
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJWT(authentication);
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(loginRequest.getUsername());
        UserDetailsImplement userDetailsImplement = (UserDetailsImplement) authentication.getPrincipal();
        LoginResponse loginResponse = new LoginResponse(
                jwt,
                userDetailsImplement.getId(),
                userDetailsImplement.getUsername(),
                userDetailsImplement.getEmail(),
                applicationUser.getDisplayName(),
                applicationUser.getAvatar()
                );
        return ResponseFactory.success(loginResponse, LoginResponse.class);
    }

    private ResponseEntity<GeneralResponse<Object>> validateSignUp(CreateAccountRequest createAccountRequest){
        if (!validationService.checkRetypePassword(createAccountRequest.getRetypePassword(),createAccountRequest.getPassword()))
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.RETYPE_ERROR);
        return validationService.validRequest(
                createAccountRequest.getUsername(),
                createAccountRequest.getPassword(),
                createAccountRequest.getEmail());
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> signupAccount(CreateAccountRequest createAccountRequest, HttpServletRequest request) {
        ResponseEntity<GeneralResponse<Object>> validateResult = validateSignUp(createAccountRequest);
        if (validateResult != null) return validateResult;
        validateResult = validationService.validExist(createAccountRequest.getUsername(), createAccountRequest.getEmail());
        if (validateResult != null) return validateResult;
        String token = getRandomNumberString();
        createAccountRequest.setPassword(bCryptPasswordEncoder.encode(createAccountRequest.getPassword()));
        String userId = addAccount(createAccountRequest);
        iRedisCaching.addOpsValue(token, userId, ttl);
        sendMail(createAccountRequest.getEmail(), token);
        return ResponseFactory.success("Waiting for verification");
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> verifySignUp(VerifyAccountRequest verifyAccountRequest){
        String token = verifyAccountRequest.getToken();
        String userId = (String) iRedisCaching.getFromOpsValue(token);
        if (userId == null)
            return ResponseFactory.error(HttpStatus.valueOf(403), ResponseStatusEnum.UNKNOWN_ERROR);
        ApplicationUser applicationUser = applicationUserRepository.findById(userId).orElse(null);
        if (applicationUser == null) return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.NOT_EXIST);
        applicationUser.setStatus(true);
        applicationUser.setAvatar(defaultAva);
        applicationUserRepository.save(applicationUser);
        iRedisCaching.removeFromOpsValue(token);
        return ResponseFactory.success(applicationUser);
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> forgotPassword(ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request){
        ApplicationUser applicationUser = applicationUserRepository.findByEmail(forgotPasswordRequest.getEmail());
        if (applicationUser == null)
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.NOT_EXIST);
        String token = getRandomNumberString();
        iRedisCaching.addOpsValue(token, applicationUser.getId(), ttl);
        sendMail(applicationUser.getEmail(), token);
        return ResponseFactory.success("A reset token has been sent to " + applicationUser.getEmail());
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        String userId = (String) iRedisCaching.getFromOpsValue(token);
        if (userId == null)
            return ResponseFactory.error(HttpStatus.valueOf(403), ResponseStatusEnum.UNKNOWN_ERROR);
        ResponseEntity<GeneralResponse<Object>> validateResult = validationService.validateChangePassword(resetPasswordRequest.getPassword(), resetPasswordRequest.getRetypePassword());
        if (validateResult != null) return validateResult;
        ApplicationUser applicationUser = applicationUserRepository.findById(userId).orElse(null);
        if (applicationUser == null) return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.NOT_EXIST);
        applicationUser.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getPassword()));
        applicationUserRepository.save(applicationUser);
        iRedisCaching.removeFromOpsValue(token);
        return ResponseFactory.success("Password has changed !");
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> findUser(String patternName, int touch){
        Pageable pageable = PageRequest.of(touch, 5 );
        List<ApplicationUser> applicationUserList = applicationUserRepository.findApplicationUsersByDisplayNameLikeOrderByIdDesc(patternName, pageable);
        return ResponseFactory.success(applicationUserList);
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> logoutAccount(){
        return ResponseFactory.success("Logout successfully!");
    }
}
