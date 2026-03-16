package com.example.wallet_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity extends BaseEntity{
    private String name;

    @OneToMany(mappedBy = "user")
    private List<UserAccountEntity> userAccounts;
}
