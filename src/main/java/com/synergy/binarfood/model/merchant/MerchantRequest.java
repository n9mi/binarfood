package com.synergy.binarfood.model.merchant;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRequest {
    @NotBlank
    String name;

    @NotBlank
    String location;

    @NotBlank
    String email;

    boolean open;
}
