package com.synergy.binarfood.service;

import com.synergy.binarfood.entity.ERole;
import com.synergy.binarfood.entity.Role;
import com.synergy.binarfood.entity.User;
import com.synergy.binarfood.model.user.UserUpdateRequest;
import com.synergy.binarfood.repository.RoleRepository;
import com.synergy.binarfood.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder tokenEncoder;

    public User createFromOidcUser(OidcUser defaultOidcUser) {
        User user = this.userRepository.findByEmail(defaultOidcUser.getAttribute("email"))
                .orElse(null);
        if (user == null) {
            Role customerRole = this.roleRepository.findByName(ERole.CUSTOMER)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role doesn't match any records"));

            user = User.builder()
                    .email(defaultOidcUser.getAttribute("email"))
                    .password(this.tokenEncoder.encode(defaultOidcUser.getIdToken().toString()))
                    .roles(List.of(customerRole))
                    .isVerified(true)
                    .build();
            this.userRepository.save(user);
        }

        return user;
    }

    public boolean isUserVerifiedByEmail(String email) {
        User user = this.userRepository.findByEmail(email)
                .orElse(null);
        if (user == null) {
            return false;
        }

        return user.isVerified();
    }

    public void update(UserUpdateRequest request) {
        User user = this.userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user didn't exists"));

        // if user change their email, then set verified as false so user can ask for otp code
        if (!Objects.equals(request.getEmail(), request.getNewEmail())) {
            user.setVerified(false);
            user.setEmail(request.getNewEmail());
        }

        user.setName(request.getName());
        this.userRepository.save(user);
    }

    public void delete(String email) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user didn't exists"));

        this.userRepository.delete(user);
    }
}
