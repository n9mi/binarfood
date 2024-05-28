package com.synergy.binarfood.service;

import com.synergy.binarfood.model.auth.LoginRequest;
import com.synergy.binarfood.model.auth.RegisterRequest;
import com.synergy.binarfood.model.auth.TokenResponse;

public interface AuthService {
    public void register(RegisterRequest request);
    public TokenResponse login(LoginRequest request);
}
