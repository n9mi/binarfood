package com.synergy.binarfood.repository;

import com.synergy.binarfood.entity.Merchant;
import com.synergy.binarfood.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAllByMerchant(Merchant merchant, Pageable pageable);

    boolean existsByIdAndMerchant_id(UUID id, UUID merchantId);

    @Modifying
    @Query(value = "delete from products", nativeQuery = true)
    void hardDeleteAll();
}

