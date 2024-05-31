package com.synergy.binarfood.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailSender;

    public void sendMail(String mailRecipient, String mailSubject, String mailMessage) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(this.mailSender);
        mail.setTo(mailRecipient);
        mail.setSubject(mailSubject);
        mail.setText(mailMessage);

        this.javaMailSender.send(mail);
    }
}
