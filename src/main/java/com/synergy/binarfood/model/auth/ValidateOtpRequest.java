package com.synergy.binarfood.model.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateOtpRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String otpCode;
}
