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
@Table(name = "assets")
public class AssetEntity extends BaseEntity{

    private String name;

    @OneToMany(mappedBy = "asset")
    private List<UserAccountEntity> userAccounts;
}
