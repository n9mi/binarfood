package com.synergy.binarfood.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank
    private String name;

    @Positive
    private double price;

    @JsonIgnore
    private UUID merchantId;

    @JsonIgnore
    @NotBlank
    private String email;
}
