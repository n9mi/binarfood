package com.synergy.binarfood.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllProductByMerchantRequest {
    private int page;
    private int pageSize;
    private UUID merchantId;
}
