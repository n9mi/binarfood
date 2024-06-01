package com.synergy.binarfood.controller.verification;

import com.synergy.binarfood.model.auth.ValidateOtpRequest;
import com.synergy.binarfood.model.web.WebResponse;
import com.synergy.binarfood.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final AuthService authService;

    @PostMapping("/otp/register")
    public ResponseEntity<WebResponse<String>> requestRegistrationOtp(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        this.authService.requestUserRegistrationOtp(userDetails.getUsername());
        WebResponse<String> response = WebResponse.<String>builder()
                .data("")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/otp/register/verify")
    public ResponseEntity<WebResponse<String>> requestRegistrationOtp(
            Authentication authentication,
            @RequestBody ValidateOtpRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setEmail(userDetails.getUsername());

        this.authService.validateUserRegistrationOtp(request);
        WebResponse<String> response = WebResponse.<String>builder()
                .data("")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/otp/forget-password")
    public ResponseEntity<WebResponse<String>> requestForgetPasswordOtp(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        this.authService.requestUserForgetPasswordOtp(userDetails.getUsername());
        WebResponse<String> response = WebResponse.<String>builder()
                .data("")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/otp/forget-password/verify")
    public ResponseEntity<WebResponse<String>> verifyForgetPasswordOtp(
            Authentication authentication,
            @RequestBody ValidateOtpRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setEmail(userDetails.getUsername());

        this.authService.validateForgetPasswordOtp(request);
        WebResponse<String> response = WebResponse.<String>builder()
                .data("")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
