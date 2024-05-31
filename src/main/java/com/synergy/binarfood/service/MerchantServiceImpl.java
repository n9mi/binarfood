package com.synergy.binarfood.service;

import com.synergy.binarfood.entity.Merchant;
import com.synergy.binarfood.entity.User;
import com.synergy.binarfood.model.merchant.MerchantRequest;
import com.synergy.binarfood.model.merchant.GetAllMerchantRequest;
import com.synergy.binarfood.model.merchant.MerchantResponse;
import com.synergy.binarfood.repository.MerchantRepository;
import com.synergy.binarfood.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public Page<MerchantResponse> findAll(GetAllMerchantRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        Page<Merchant> merchants;

        if (!Objects.equals(request.getEmail(), "")) {
            merchants = this.merchantRepository.findAllByUser_Email(request.getEmail(), pageable);
        } else if (request.isOnlyOpen()) {
            merchants = this.merchantRepository.findOpenedMerchants(pageable);
        } else {
            merchants = this.merchantRepository.findAll(pageable);
        }

        return merchants.map(new Function<Merchant, MerchantResponse>() {
            @Override
            public MerchantResponse apply(Merchant merchant) {
                return MerchantResponse.builder()
                        .id(merchant.getId().toString())
                        .name(merchant.getName())
                        .open(merchant.isOpen())
                        .location(merchant.getLocation())
                        .build();
            }
        });
    }

    public MerchantResponse create(MerchantRequest request) {
        this.validationService.validate(request);

        User user = this.userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user doesn't exists"));
        Merchant merchant = Merchant.builder()
                .name(request.getName())
                .location(request.getLocation())
                .open(request.isOpen())
                .user(user)
                .build();
        this.merchantRepository.save(merchant);

        return MerchantResponse.builder()
                .id(merchant.getId().toString())
                .name(merchant.getName())
                .location(merchant.getLocation())
                .open(merchant.isOpen())
                .build();
    }
}
