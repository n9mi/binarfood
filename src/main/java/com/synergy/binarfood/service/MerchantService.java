package com.synergy.binarfood.service;

import com.synergy.binarfood.model.merchant.*;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface MerchantService {
    Page<MerchantResponse> findAll(GetAllMerchantRequest request);
    public MerchantResponse create(MerchantRequest request);
    MerchantResponse update(UUID merchantId, MerchantRequest request);
    public MerchantResponse updateOpen(UUID merchantId, OpenMerchantRequest request);
    public void delete(UUID merchantId, DeleteMerchantRequest request);
}
