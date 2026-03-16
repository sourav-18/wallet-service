package com.example.wallet_service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_accounts")
public class UserAccountEntity extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "assert_id",nullable = false)
    private AssetEntity asset;

    private Double amount;
}
