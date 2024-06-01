package com.synergy.binarfood.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordRequest {
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 5, max = 100)
    private String newPassword;
}
