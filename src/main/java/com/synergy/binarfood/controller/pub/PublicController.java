package com.synergy.binarfood.controller.pub;

import com.synergy.binarfood.model.merchant.GetAllMerchantRequest;
import com.synergy.binarfood.model.merchant.MerchantResponse;
import com.synergy.binarfood.model.web.WebResponse;
import com.synergy.binarfood.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {
    private final MerchantService merchantService;

    @GetMapping("/merchants")
    public ResponseEntity<WebResponse<List<MerchantResponse>>> getAll(
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
}
