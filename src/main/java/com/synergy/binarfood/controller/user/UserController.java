package com.synergy.binarfood.controller.user;

import com.synergy.binarfood.model.merchant.MerchantRequest;
import com.synergy.binarfood.model.merchant.MerchantResponse;
import com.synergy.binarfood.model.user.UserUpdateRequest;
import com.synergy.binarfood.model.web.WebResponse;
import com.synergy.binarfood.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<String>> create(
            Authentication authentication,
            @RequestBody UserUpdateRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setEmail(userDetails.getUsername());

        this.userService.update(request);

        WebResponse<String> response = WebResponse.<String>builder()
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<String>> create(
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        this.userService.delete(userDetails.getUsername());

        WebResponse<String> response = WebResponse.<String>builder()
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
