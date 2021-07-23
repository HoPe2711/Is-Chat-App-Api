package com.example.chat_app_service.authen_service.mailsender;
public interface EmailService {
    void sendEmail(String from, String to, String subject, String body);
}
