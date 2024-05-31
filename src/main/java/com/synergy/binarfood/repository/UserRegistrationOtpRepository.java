package com.synergy.binarfood.repository;

import com.synergy.binarfood.entity.UserRegistrationOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRegistrationOtpRepository extends JpaRepository<UserRegistrationOtp, UUID> {
    Optional<UserRegistrationOtp> findByUser_Email(String email);
}
