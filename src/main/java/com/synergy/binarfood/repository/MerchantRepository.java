package com.synergy.binarfood.repository;

import com.synergy.binarfood.entity.Merchant;
import com.synergy.binarfood.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    @Query(value = "select * from merchants m where m.open is true", nativeQuery = true)
    Page<Merchant> findOpenedMerchants(Pageable pageable);

    Page<Merchant> findAllByUser_Email(String email, Pageable pageable);

    boolean existsByIdAndUser_Email(UUID id, String email);

    boolean existsByIdAndOpen(UUID id, boolean open);

    @Modifying
    @Query(value = "delete from merchants", nativeQuery = true)
    void hardDeleteAll();
}
