package com.synergy.binarfood.controller.merchant;

import com.synergy.binarfood.model.web.WebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
public class MerchantController {

    @GetMapping("/merchants")
    public ResponseEntity<WebResponse<String>> getAll() {
        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
