package com.synergy.binarfood.security.filter;

import com.synergy.binarfood.security.service.JWTService;
import com.synergy.binarfood.security.service.UserDetailsServiceImpl;
import com.synergy.binarfood.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    public final JWTService jwtService;
    public final UserDetailsServiceImpl userDetailsService;
    public final HandlerExceptionResolver handlerExceptionResolver;
    public final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull  FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String email = this.jwtService.extractEmail(token);
            if (email != null || SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                if (this.jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    if (!request.getRequestURI().startsWith("/api/v1/verification") &&
                        !this.userService.isUserVerifiedByEmail(userDetails.getUsername())) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user haven't verified yet");
                    }

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    filterChain.doFilter(request, response);
                }
            }
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
