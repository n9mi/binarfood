package com.synergy.binarfood.controller.pub;

import com.synergy.binarfood.model.merchant.GetAllMerchantRequest;
import com.synergy.binarfood.model.merchant.MerchantResponse;
import com.synergy.binarfood.model.product.GetAllProductByMerchantRequest;
import com.synergy.binarfood.model.product.ProductResponse;
import com.synergy.binarfood.model.web.WebResponse;
import com.synergy.binarfood.service.MerchantService;
import com.synergy.binarfood.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {
    private final MerchantService merchantService;
    private final ProductService productService;

    @GetMapping("/merchants")
    public ResponseEntity<WebResponse<List<MerchantResponse>>> getAllMerchant(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "onlyOpen", required = false, defaultValue = "true") boolean onlyOpen) {
        GetAllMerchantRequest request = GetAllMerchantRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .email("")
                .onlyOpen(onlyOpen)
                .build();
        Page<MerchantResponse> merchants = this.merchantService
                .findAll(request);
        WebResponse<List<MerchantResponse>> response = WebResponse.<List<MerchantResponse>>builder()
                .data(merchants.getContent())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/merchants/{merchantId}/products")
    public ResponseEntity<WebResponse<List<ProductResponse>>> getAllProductByMerchant(
            @PathVariable UUID merchantId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        GetAllProductByMerchantRequest request = GetAllProductByMerchantRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .merchantId(merchantId)
                .build();
        Page<ProductResponse> products = this.productService
                .findAllByMerchant(request);
        WebResponse<List<ProductResponse>> response = WebResponse.<List<ProductResponse>>builder()
                .data(products.getContent())
                .build();

        return ResponseEntity.ok(response);
    }
}
