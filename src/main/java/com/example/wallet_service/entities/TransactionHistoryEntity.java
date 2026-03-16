package com.example.wallet_service.entities;

import com.example.wallet_service.entities.type.TxnType;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "transaction_histories")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryEntity extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "assert_id",nullable = false)
    private AssetEntity asset;

    @ManyToOne
    @JoinColumn(name = "system_account_id")
    private SystemAccountEntity systemAccount;

    private Double amount;

    @Column(name = "txn_type")
    @Enumerated(EnumType.STRING)
    private TxnType txnType;

    private String reason;
}
