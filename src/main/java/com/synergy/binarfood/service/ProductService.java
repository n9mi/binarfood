package com.synergy.binarfood.service;

import com.synergy.binarfood.model.product.DeleteProductRequest;
import com.synergy.binarfood.model.product.GetAllProductByMerchantRequest;
import com.synergy.binarfood.model.product.ProductRequest;
import com.synergy.binarfood.model.product.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ProductService {
    public Page<ProductResponse> findAllByMerchant(GetAllProductByMerchantRequest request);
    public ProductResponse create(ProductRequest request);
    public ProductResponse update(UUID productId, ProductRequest request);
    public void delete(UUID productId, DeleteProductRequest request);
}
