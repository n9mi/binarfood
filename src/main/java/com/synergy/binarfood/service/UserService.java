package com.synergy.binarfood.service;

import com.synergy.binarfood.entity.User;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface UserService {
    public User createFromOidcUser(OidcUser defaultOidcUser);
    public boolean isUserVerifiedByEmail(String email);
}
