package com.synergy.binarfood.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllMerchantRequest {
    private int page;
    private int pageSize;
    private boolean onlyOpen;
    private String email;
}
