package com.synergy.binarfood.controller.merchant;

import com.synergy.binarfood.model.merchant.MerchantRequest;
import com.synergy.binarfood.model.merchant.GetAllMerchantRequest;
import com.synergy.binarfood.model.merchant.MerchantResponse;
import com.synergy.binarfood.model.web.WebResponse;
import com.synergy.binarfood.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchant/merchants")
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantService merchantService;

    @GetMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<MerchantResponse>>> getAll(
            Authentication authentication,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "onlyOpen", required = false, defaultValue = "true") boolean onlyOpen) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        GetAllMerchantRequest request = GetAllMerchantRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .email(userDetails.getUsername())
                .onlyOpen(onlyOpen)
                .build();
        Page<MerchantResponse> merchants = this.merchantService
                .findAll(request);
        WebResponse<List<MerchantResponse>> response = WebResponse.<List<MerchantResponse>>builder()
                .data(merchants.getContent())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<MerchantResponse>> create(
            Authentication authentication,
            @RequestBody MerchantRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setEmail(userDetails.getUsername());

        MerchantResponse merchant = this.merchantService.create(request);
        WebResponse<MerchantResponse> response = WebResponse.<MerchantResponse>builder()
                .data(merchant)
                .build();

        return ResponseEntity.ok(response);
    }
}
