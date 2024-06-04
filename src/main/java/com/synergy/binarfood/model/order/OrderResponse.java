package com.synergy.binarfood.model.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    String id;
    String destinationAddress;
    Date orderAt;
    Date completedAt;
    double totalPrice;
    List<OrderDetailResponse> orderDetails;
}