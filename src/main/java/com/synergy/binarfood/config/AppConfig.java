package com.synergy.binarfood.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    @Bean
    Random random() {
        return new Random();
    }
}
