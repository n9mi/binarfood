package com.synergy.binarfood.service;

import com.synergy.binarfood.entity.*;
import com.synergy.binarfood.model.auth.*;
import com.synergy.binarfood.repository.RoleRepository;
import com.synergy.binarfood.repository.UserChangePasswordRepository;
import com.synergy.binarfood.repository.UserRegistrationOtpRepository;
import com.synergy.binarfood.repository.UserRepository;
import com.synergy.binarfood.security.service.JWTService;
import com.synergy.binarfood.security.user.UserDetailsImpl;
import com.synergy.binarfood.util.random.Randomizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationOtpRepository userRegistrationOtpRepository;
    private final UserChangePasswordRepository userChangePasswordRepository;
    private final JWTService jwtService;
    private final ValidationService validationService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

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
                .isVerified(false)
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

    @Transactional
    public void requestUserRegistrationOtp(String userEmail) {
        User user = this.userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
        if (user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user already verified");
        }

        String otpCode = Randomizer.generateOtp(6);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);

        UserRegistrationOtp userOtp = this.userRegistrationOtpRepository
                .findByUser_Email(userEmail)
                .orElse(UserRegistrationOtp.builder()
                        .user(user)
                        .build());
        userOtp.setOtpCode(BCrypt.hashpw(otpCode, BCrypt.gensalt()));
        userOtp.setExpiredAt(calendar.getTime());
        this.userRegistrationOtpRepository.save(userOtp);

        mailService.sendMail(userEmail, "Registration OTP code from Binarfood", otpCode);
    }

    @Transactional
    public void validateUserRegistrationOtp(ValidateOtpRequest request) {
        User user = this.userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
        if (user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user already verified");
        }

        UserRegistrationOtp userOtp = this.userRegistrationOtpRepository
                .findByUser_Email(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "something wrong"));
        if (userOtp.getExpiredAt().before(new Date())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "expired code");
        }
        if (!BCrypt.checkpw(request.getOtpCode(), userOtp.getOtpCode())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid code");
        }

        user.setVerified(true);
        this.userRepository.save(user);

        this.userRegistrationOtpRepository.delete(userOtp);
    }

    @Transactional
    public void requestUserChangePasswordOtp(String userEmail) {
        User user = this.userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
        if (!user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user haven't verified yet");
        }

        String otpCode = Randomizer.generateOtp(6);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);

        List<UserChangePassword> userChangePasswords = this.userChangePasswordRepository
                .findByUser_EmailAndExpiredAtBefore(userEmail, new Date());
        userChangePasswords = userChangePasswords.stream().map(ucp -> {
            ucp.setMarkedAsValid(false);
            return ucp;
        }).toList();
        this.userChangePasswordRepository.saveAllAndFlush(userChangePasswords);

        UserChangePassword newUserChangePassword = UserChangePassword.builder()
                .user(user)
                .otpCode(BCrypt.hashpw(otpCode, BCrypt.gensalt()))
                .expiredAt(new Date())
                .markedAsValid(true)
                .build();
        this.userChangePasswordRepository.save(newUserChangePassword);

        mailService.sendMail(userEmail, "Change password OTP code from Binarfood", otpCode);
    }

    @Transactional
    public void validateUserChangePasswordOtp(ValidateOtpRequest request) {
        this.validationService.validate(request);

        User user = this.userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
        if (!user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user haven't verified yet");
        }

        UserChangePassword userChangePassword = this.userChangePasswordRepository
                .findLastValid(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "expired code"));
        if (!BCrypt.checkpw(request.getOtpCode(), userChangePassword.getOtpCode())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid code");
        }
        userChangePassword.setMarkedAsValid(false);
        this.userChangePasswordRepository.save(userChangePassword);
    }
}
