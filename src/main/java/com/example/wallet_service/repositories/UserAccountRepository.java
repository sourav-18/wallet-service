package com.example.wallet_service.repositories;

import com.example.wallet_service.dtos.UserAccountDetailsDto;
import com.example.wallet_service.entities.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {

    @Query("""
            SELECT new com.example.wallet_service.dtos.UserAccountDetailsDto(U.id, U.asset.id, U.amount)
            FROM UserAccountEntity AS U
            WHERE U.asset.id=:assetId AND U.user.id=:userId
            """)
    Optional<UserAccountDetailsDto> accountDetails(@Param("assetId") Long assetId, @Param("userId") Long userId);

    @Query("""
            SELECT new com.example.wallet_service.dtos.UserAccountDetailsDto(U.id, U.asset.id, U.amount)
            FROM UserAccountEntity AS U
            WHERE U.user.id=:userId
            """)
    List<UserAccountDetailsDto> allAccountDetails(@Param("userId") Long userId);

    @Modifying(clearAutomatically=true)
    @Query("UPDATE UserAccountEntity SET amount=amount+:amount WHERE id=:id")
    Integer credit(@Param("id") Long id, @Param("amount") Double amount);

    @Modifying(clearAutomatically=true)
    @Query("UPDATE UserAccountEntity SET amount=amount-:amount WHERE id=:id AND amount>=:amount")
    Integer debit(@Param("id") Long id, @Param("amount") Double amount);
}
