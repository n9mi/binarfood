package com.synergy.binarfood.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    @Bean
    public Random random() {
        return new Random();
    }
}
