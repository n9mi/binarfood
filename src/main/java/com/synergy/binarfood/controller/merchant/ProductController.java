package com.synergy.binarfood.controller.merchant;

import com.synergy.binarfood.model.merchant.MerchantRequest;
import com.synergy.binarfood.model.merchant.MerchantResponse;
import com.synergy.binarfood.model.product.DeleteProductRequest;
import com.synergy.binarfood.model.product.ProductRequest;
import com.synergy.binarfood.model.product.ProductResponse;
import com.synergy.binarfood.model.web.WebResponse;
import com.synergy.binarfood.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(
            path = "/{merchantId}/products",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<ProductResponse>> create(
            Authentication authentication,
            @PathVariable UUID merchantId,
            @RequestBody ProductRequest request) {
        request.setMerchantId(merchantId);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setEmail(userDetails.getUsername());

        ProductResponse product = this.productService.create(request);

        WebResponse<ProductResponse> response = WebResponse.<ProductResponse>builder()
                .data(product)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping(
            path = "/{merchantId}/products/{productId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<ProductResponse>> update(
            Authentication authentication,
            @PathVariable UUID merchantId,
            @PathVariable UUID productId,
            @RequestBody ProductRequest request) {
        request.setMerchantId(merchantId);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setEmail(userDetails.getUsername());

        ProductResponse product = this.productService.update(productId, request);

        WebResponse<ProductResponse> response = WebResponse.<ProductResponse>builder()
                .data(product)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            path = "/{merchantId}/products/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<String >> delete(
            Authentication authentication,
            @PathVariable UUID merchantId,
            @PathVariable UUID productId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        DeleteProductRequest request = DeleteProductRequest.builder()
                .merchantId(merchantId)
                .email(userDetails.getUsername())
                .build();

        this.productService.delete(productId, request);

        WebResponse<String> response = WebResponse.<String>builder()
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
