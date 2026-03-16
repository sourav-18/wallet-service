package com.example.wallet_service.mappers;

import com.example.wallet_service.entities.AssetEntity;
import com.example.wallet_service.entities.TransactionHistoryEntity;
import com.example.wallet_service.entities.UserEntity;
import com.example.wallet_service.entities.type.TxnType;

public class TransactionHistoryMapper {

    public static TransactionHistoryEntity EntityForUser(UserEntity user,
                                           AssetEntity asset,
                                           TxnType txnType,
                                           Double amount,
                                           String reason){
        return TransactionHistoryEntity.builder()
                .user(user)
                .asset(asset)
                .amount(amount)
                .txnType(txnType)
                .reason(reason)
                .build();
    }

}
