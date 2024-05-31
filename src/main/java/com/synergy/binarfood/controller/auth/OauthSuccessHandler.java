package com.synergy.binarfood.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synergy.binarfood.entity.User;
import com.synergy.binarfood.model.auth.TokenResponse;
import com.synergy.binarfood.security.service.JWTService;
import com.synergy.binarfood.security.user.UserDetailsImpl;
import com.synergy.binarfood.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class OauthSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;
    private final JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        User user = this.userService.createFromOidcUser(oidcUser);
        UserDetails userDetails = UserDetailsImpl.build(user);

        String token = this.jwtService.generateToken(userDetails);
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(token)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String tokenResponseString = objectMapper.writeValueAsString(tokenResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(tokenResponseString);
        out.flush();
    }
}
