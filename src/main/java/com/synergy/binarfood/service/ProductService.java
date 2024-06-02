package com.synergy.binarfood.service;

import com.synergy.binarfood.model.product.GetAllProductByMerchantRequest;
import com.synergy.binarfood.model.product.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {
    public Page<ProductResponse> findAllByMerchant(GetAllProductByMerchantRequest request);
}
