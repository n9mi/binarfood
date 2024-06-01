package com.synergy.binarfood.service;

import com.synergy.binarfood.model.auth.*;

public interface AuthService {
    public void register(RegisterRequest request);
    public TokenResponse login(LoginRequest request);
    public void requestUserRegistrationOtp(String userEmail);
    public void validateUserRegistrationOtp(ValidateOtpRequest request);
    public void requestUserForgetPasswordOtp(String userEmail);
    public void validateForgetPasswordOtp(ValidateOtpRequest request);
    public void forgetPassword(ForgetPasswordRequest request);
}
