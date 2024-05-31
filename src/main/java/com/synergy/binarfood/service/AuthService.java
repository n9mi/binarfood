package com.synergy.binarfood.service;

import com.synergy.binarfood.model.auth.LoginRequest;
import com.synergy.binarfood.model.auth.RegisterRequest;
import com.synergy.binarfood.model.auth.TokenResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface AuthService {
    public void register(RegisterRequest request);
    public TokenResponse login(LoginRequest request);
}
