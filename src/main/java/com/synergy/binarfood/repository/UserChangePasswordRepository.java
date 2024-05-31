package com.synergy.binarfood.repository;

import com.synergy.binarfood.entity.UserChangePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserChangePasswordRepository extends JpaRepository<UserChangePassword, UUID> {
    List<UserChangePassword> findByUser_EmailAndExpiredAtBefore(String email, Date beforeDate);

    @Query(
            value = """
                        select ucp.* from user_change_password ucp
                        inner join users u on u.id = ucp.user_id
                        where u.email = :userEmail
                        and ucp.expired_at > now()
                        and ucp.marked_as_valid = true
                        limit 1
                    """,
            nativeQuery = true)
    Optional<UserChangePassword> findLastValid(@Param("userEmail") String email);
}
