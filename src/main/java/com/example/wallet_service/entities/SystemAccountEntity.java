package com.example.wallet_service.entities;

import com.example.wallet_service.entities.type.SystemAccountType;

import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "system_accounts")
public class SystemAccountEntity extends BaseEntity{

    @Column(name = "account_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private SystemAccountType accountType;

    private Double amount;
}
