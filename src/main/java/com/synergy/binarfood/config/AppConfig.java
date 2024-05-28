package com.synergy.binarfood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import java.util.Random;

@Configuration
public class AppConfig {

    @Bean
    Random random() {
        return new Random();
    }
}
