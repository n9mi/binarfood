package com.synergy.binarfood.model.merchant;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @NotBlank
    String email;

    boolean open;
}
