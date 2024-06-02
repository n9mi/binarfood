package com.synergy.binarfood.service;

import com.synergy.binarfood.entity.Merchant;
import com.synergy.binarfood.entity.Product;
import com.synergy.binarfood.model.product.DeleteProductRequest;
import com.synergy.binarfood.model.product.GetAllProductByMerchantRequest;
import com.synergy.binarfood.model.product.ProductRequest;
import com.synergy.binarfood.model.product.ProductResponse;
import com.synergy.binarfood.repository.MerchantRepository;
import com.synergy.binarfood.repository.ProductRepository;
import com.synergy.binarfood.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public Page<ProductResponse> findAllByMerchant(GetAllProductByMerchantRequest request) {
        Merchant merchant = this.merchantRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "merchant doesn't exists"));

        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        Page<Product> products = this.productRepository.findAllByMerchant(merchant, pageable);

        return products.map(new Function<Product, ProductResponse>() {
            @Override
            public ProductResponse apply(Product product) {
                return ProductResponse.builder()
                        .id(product.getId().toString())
                        .name(product.getName())
                        .price(product.getPrice())
                        .build();
            }
        });
    }

    public ProductResponse create(ProductRequest request) {
        this.validationService.validate(request);

        Merchant merchant = this.merchantRepository
                .findById(request.getMerchantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "merchant doesn't exists"));
        if (!this.merchantRepository.existsByIdAndUser_Email(merchant.getId(), request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "merchant doesn't exists");
        }

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .merchant(merchant)
                .build();
        this.productRepository.save(product);

        return ProductResponse.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }

    public ProductResponse update(UUID productId, ProductRequest request) {
        this.validationService.validate(request);

        Merchant merchant = this.merchantRepository
                .findById(request.getMerchantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "merchant doesn't exists found"));
        if (!this.merchantRepository.existsByIdAndUser_Email(merchant.getId(), request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "merchant doesn't exists");
        }

        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product doesn't exists"));
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        this.productRepository.save(product);

        return ProductResponse.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }

    public void delete(UUID productId, DeleteProductRequest request) {
        this.validationService.validate(request);

        Merchant merchant = this.merchantRepository
                .findById(request.getMerchantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "merchant doesn't exists found"));
        if (!this.merchantRepository.existsByIdAndUser_Email(merchant.getId(), request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "merchant doesn't exists");
        }

        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product doesn't exists"));
        this.productRepository.delete(product);
    }
}
