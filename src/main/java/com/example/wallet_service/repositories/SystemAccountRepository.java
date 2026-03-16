package com.example.wallet_service.repositories;

import com.example.wallet_service.dtos.SystemAccountDetailsDto;
import com.example.wallet_service.dtos.UserAccountDetailsDto;
import com.example.wallet_service.entities.SystemAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccountEntity,Long> {

    @Query("""
            SELECT new com.example.wallet_service.dtos.SystemAccountDetailsDto(S.id, S.amount)
            FROM SystemAccountEntity AS S
            WHERE S.id=:id
            """)
    Optional<SystemAccountDetailsDto> accountDetails(@Param("id") Long id);

    @Modifying(clearAutomatically=true)
    @Query("UPDATE SystemAccountEntity SET amount=amount+:amount WHERE id=:id")
    Integer credit(@Param("id") Long id, @Param("amount") Double amount);

    @Modifying(clearAutomatically=true)
    @Query("UPDATE SystemAccountEntity SET amount=amount-:amount WHERE id=:id AND amount>=:amount")
    Integer debit(@Param("id") Long id, @Param("amount") Double amount);
}
