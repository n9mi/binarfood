package com.synergy.binarfood.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UpdateOrderDetailRequest {
    @JsonIgnore
    private String email;

    @JsonIgnore
    private UUID orderId;

    @Positive
    private int quantity;
}
