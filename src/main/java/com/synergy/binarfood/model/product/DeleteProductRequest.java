package com.synergy.binarfood.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProductRequest {
    @JsonIgnore
    private UUID merchantId;

    @JsonIgnore
    @NotBlank
    private String email;
}
