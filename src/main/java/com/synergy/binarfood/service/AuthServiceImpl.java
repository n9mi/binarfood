package com.synergy.binarfood.service;

import com.synergy.binarfood.entity.ERole;
import com.synergy.binarfood.entity.Role;
import com.synergy.binarfood.entity.User;
import com.synergy.binarfood.model.auth.LoginRequest;
import com.synergy.binarfood.model.auth.RegisterRequest;
import com.synergy.binarfood.model.auth.TokenResponse;
import com.synergy.binarfood.repository.RoleRepository;
import com.synergy.binarfood.repository.UserRepository;
import com.synergy.binarfood.security.service.JWTService;
import com.synergy.binarfood.security.user.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final ValidationService validationService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void register(RegisterRequest request) {
        this.validationService.validate(request);

        if (this.userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("user with email %s already exists", request.getEmail()));
        }

        if (!EnumUtils.isValidEnum(ERole.class, request.getAsRole())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "role didn't match any records");
        }
        Role role = this.roleRepository.findByName(ERole.valueOf(request.getAsRole()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role didn't match any records"));

        User user = User
                .builder()
                .name(request.getName())
                .email(request.getEmail())
                .roles(List.of(role))
                .password(this.passwordEncoder.encode(request.getPassword()))
                .build();
        this.userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        this.validationService.validate(request);

        User user = this.userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        String token = this.jwtService.generateToken(UserDetailsImpl.build(user));

        return TokenResponse.builder()
                .accessToken(token)
                .build();
    }
}
