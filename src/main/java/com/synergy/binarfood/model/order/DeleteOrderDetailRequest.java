package com.synergy.binarfood.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteOrderDetailRequest {
    @JsonIgnore
    private String email;

    @JsonIgnore
    private UUID orderId;
}
