package com.synergy.binarfood.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailRequest {
    @JsonIgnore
    private String email;

    @JsonIgnore
    private UUID orderId;

    @NotBlank
    private String productId;

    @Positive
    private int quantity;
}