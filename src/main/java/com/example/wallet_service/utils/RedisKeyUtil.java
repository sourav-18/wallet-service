package com.example.wallet_service.utils;

import java.time.LocalDate;

public class RedisKeyUtil {
    private final static String txnLock = "txnLock";
    private final static String idempotency="idempotency";

    private static String getDateHashCode(LocalDate date) {
        return date.toString();
    }

    public static String getTxnLock(String userId) {
        return txnLock+":"+userId;
    }

    public static String getTxnIdempotencyKey(String key) {
        return idempotency+":"+key;
    }


}
