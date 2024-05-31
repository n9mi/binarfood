package com.synergy.binarfood.service;

import com.synergy.binarfood.model.merchant.GetAllMerchantRequest;
import com.synergy.binarfood.model.merchant.MerchantRequest;
import com.synergy.binarfood.model.merchant.MerchantResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface MerchantService {
    Page<MerchantResponse> findAll(GetAllMerchantRequest request);
    public MerchantResponse create(MerchantRequest request);
}
