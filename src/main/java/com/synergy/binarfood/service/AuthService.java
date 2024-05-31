package com.synergy.binarfood.service;

import com.synergy.binarfood.model.auth.LoginRequest;
import com.synergy.binarfood.model.auth.RegisterRequest;
import com.synergy.binarfood.model.auth.TokenResponse;
import com.synergy.binarfood.model.auth.ValidateOtpRequest;

public interface AuthService {
    public void register(RegisterRequest request);
    public TokenResponse login(LoginRequest request);
    public void requestUserRegistrationOtp(String userEmail);
    public void validateUserRegistrationOtp(ValidateOtpRequest request);
    public void requestUserChangePasswordOtp(String userEmail);
    public void validateUserChangePasswordOtp(ValidateOtpRequest request);
}
