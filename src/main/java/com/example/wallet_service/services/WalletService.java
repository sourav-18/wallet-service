package com.example.wallet_service.services;

import com.example.wallet_service.dtos.SystemAccountDetailsDto;
import com.example.wallet_service.dtos.UserAccountDetailsDto;
import com.example.wallet_service.dtos.request.BonusRequestDto;
import com.example.wallet_service.dtos.request.PurchaseRequestDto;
import com.example.wallet_service.dtos.request.TopupRequestDto;
import com.example.wallet_service.dtos.response.ApiResponse;
import com.example.wallet_service.entities.TransactionHistoryEntity;
import com.example.wallet_service.entities.type.TxnType;
import com.example.wallet_service.exceptions.AccountCreditFailedException;
import com.example.wallet_service.exceptions.AccountNotFoundException;
import com.example.wallet_service.exceptions.ConcurrentTxnOperationException;
import com.example.wallet_service.exceptions.InsufficientFundsException;
import com.example.wallet_service.mappers.ResponseMapper;
import com.example.wallet_service.repositories.*;
import com.example.wallet_service.utils.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserAccountRepository userAccountRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final SystemAccountRepository systemAccountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final RedisService redisService;


    private static final long lockExpiryTime = 60 * 60; //1 hour
    private static final long idempotencyExpiryTime = 60 * 60; //2 hour
    private static final long systemTreasuryAccount = 2;
    private static final long systemRevenueAccount = 1;

    private void acquireLockAndRegisterRelease(String lockKey) {
        boolean isLock = redisService.setNx(lockKey, lockExpiryTime);
        if (!isLock) {
            throw new ConcurrentTxnOperationException("Concurrent operation");
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        redisService.removeKey(lockKey);
                    }
                }
        );
    }

    @Transactional
    public ApiResponse<Void> topup(TopupRequestDto requestDto, String idempotencyKey) {
        String txnAction = "topup";
        String lockKey = RedisKeyUtil.getTxnLock(requestDto.getUserId().toString());

        acquireLockAndRegisterRelease(lockKey);

        String redisIdempotencyKey = RedisKeyUtil.getTxnIdempotencyKey(idempotencyKey);
        if (redisService.get(redisIdempotencyKey) != null) {
            return ResponseMapper.success(
                    HttpStatus.OK.value(),
                    "topup amount credit successfully"
            );
        }

        UserAccountDetailsDto accountDetails = userAccountRepository.accountDetails(requestDto.getAssetId(), requestDto.getUserId())
                .orElseThrow(() -> new AccountNotFoundException("User account not Found"));

        Integer userCreditUpdateRow = userAccountRepository
                .credit(accountDetails.getId(), requestDto.getAmount());
        if (userCreditUpdateRow == 0) {
            throw new AccountCreditFailedException("User account credit failed");
        }

        Integer CustomerCreditUpdateRow = systemAccountRepository
                .credit(systemRevenueAccount, requestDto.getAmount());
        if (CustomerCreditUpdateRow == 0) {
            throw new AccountCreditFailedException("system account credit failed");
        }

        TransactionHistoryEntity userCreditTxnHistory = TransactionHistoryEntity.builder()
                .user(userRepository.getReferenceById(requestDto.getUserId()))
                .asset(assetRepository.getReferenceById(requestDto.getAssetId()))
                .amount(requestDto.getAmount())
                .txnType(TxnType.Credit)
                .reason(txnAction)
                .build();

        TransactionHistoryEntity systemCreditTxnHistory = TransactionHistoryEntity.builder()
                .systemAccount(systemAccountRepository.getReferenceById(systemRevenueAccount))
                .asset(assetRepository.getReferenceById(requestDto.getAssetId()))
                .amount(requestDto.getAmount())
                .txnType(TxnType.Credit)
                .reason(txnAction)
                .build();

        transactionHistoryRepository.saveAll(List.of(userCreditTxnHistory, systemCreditTxnHistory));
        redisService.set(redisIdempotencyKey, "ok", idempotencyExpiryTime);

        return ResponseMapper.success(
                HttpStatus.CREATED.value(),
                "topup amount credit successfully"
        );

    }

    @Transactional
    public ApiResponse<Void> bonus(BonusRequestDto requestDto, String idempotencyKey) {
        String txnAction = "bonus";
        String lockKey = RedisKeyUtil.getTxnLock(requestDto.getUserId().toString());

        acquireLockAndRegisterRelease(lockKey);

        String redisIdempotencyKey = RedisKeyUtil.getTxnIdempotencyKey(idempotencyKey);
        if (redisService.get(redisIdempotencyKey) != null) {
            return ResponseMapper.success(
                    HttpStatus.OK.value(),
                    "bonus amount credit successfully"
            );
        }

            SystemAccountDetailsDto systemAccountDetails = systemAccountRepository.accountDetails(systemTreasuryAccount)
                    .orElseThrow(() -> new AccountNotFoundException("system account not Found"));

            if (systemAccountDetails.getAmount() < requestDto.getAmount()) {
                throw new InsufficientFundsException("Insufficient balance in System account");
            }

            Integer CustomerCreditUpdateRow = systemAccountRepository
                    .debit(systemTreasuryAccount, requestDto.getAmount());
            if (CustomerCreditUpdateRow == 0) {
                throw new AccountCreditFailedException("system account debit failed");
            }

            UserAccountDetailsDto accountDetails = userAccountRepository.accountDetails(requestDto.getAssetId(), requestDto.getUserId())
                    .orElseThrow(() -> new AccountNotFoundException("User account not Found"));

            Integer userCreditUpdateRow = userAccountRepository
                    .credit(accountDetails.getId(), requestDto.getAmount());
            if (userCreditUpdateRow == 0) {
                throw new AccountCreditFailedException("User account credit failed");
            }

            TransactionHistoryEntity userCreditTxnHistory = TransactionHistoryEntity.builder()
                    .user(userRepository.getReferenceById(requestDto.getUserId()))
                    .asset(assetRepository.getReferenceById(requestDto.getAssetId()))
                    .amount(requestDto.getAmount())
                    .txnType(TxnType.Credit)
                    .reason(txnAction)
                    .build();

            TransactionHistoryEntity systemCreditTxnHistory = TransactionHistoryEntity.builder()
                    .systemAccount(systemAccountRepository.getReferenceById(systemTreasuryAccount))
                    .asset(assetRepository.getReferenceById(requestDto.getAssetId()))
                    .amount(requestDto.getAmount())
                    .txnType(TxnType.Debit)
                    .reason(txnAction)
                    .build();

            transactionHistoryRepository.saveAll(List.of(userCreditTxnHistory, systemCreditTxnHistory));
            redisService.set(redisIdempotencyKey, "ok", idempotencyExpiryTime);

        return ResponseMapper.success(
                HttpStatus.CREATED.value(),
                "bonus amount credit successfully"
        );

    }

    @Transactional
    public ApiResponse<Void> purchase(PurchaseRequestDto requestDto, String idempotencyKey) {
        String txnAction = "purchase";
        String lockKey = RedisKeyUtil.getTxnLock(requestDto.getUserId().toString());

        acquireLockAndRegisterRelease(lockKey);

        String redisIdempotencyKey = RedisKeyUtil.getTxnIdempotencyKey(idempotencyKey);
        if (redisService.get(redisIdempotencyKey) != null) {
            return ResponseMapper.success(
                    HttpStatus.OK.value(),
                    "purchase amount debit successfully"
            );
        }

        try {
            UserAccountDetailsDto accountDetails = userAccountRepository.accountDetails(requestDto.getAssetId(), requestDto.getUserId())
                    .orElseThrow(() -> new AccountNotFoundException("User account not Found"));

            if (accountDetails.getAmount() < requestDto.getAmount()) {
                throw new InsufficientFundsException("Insufficient balance to complete the purchase");
            }

            Integer userCreditUpdateRow = userAccountRepository
                    .debit(accountDetails.getId(), requestDto.getAmount());
            if (userCreditUpdateRow == 0) {
                throw new AccountCreditFailedException("User account debit failed");
            }

            Integer CustomerCreditUpdateRow = systemAccountRepository
                    .credit(systemRevenueAccount, requestDto.getAmount());
            if (CustomerCreditUpdateRow == 0) {
                throw new AccountCreditFailedException("system account credit failed");
            }

            TransactionHistoryEntity userCreditTxnHistory = TransactionHistoryEntity.builder()
                    .user(userRepository.getReferenceById(requestDto.getUserId()))
                    .asset(assetRepository.getReferenceById(requestDto.getAssetId()))
                    .amount(requestDto.getAmount())
                    .txnType(TxnType.Debit)
                    .reason(txnAction)
                    .build();

            TransactionHistoryEntity systemCreditTxnHistory = TransactionHistoryEntity.builder()
                    .systemAccount(systemAccountRepository.getReferenceById(systemRevenueAccount))
                    .asset(assetRepository.getReferenceById(requestDto.getAssetId()))
                    .amount(requestDto.getAmount())
                    .txnType(TxnType.Credit)
                    .reason(txnAction)
                    .build();

            transactionHistoryRepository.saveAll(List.of(userCreditTxnHistory, systemCreditTxnHistory));
            redisService.set(redisIdempotencyKey, "ok", idempotencyExpiryTime);
        } finally {
            redisService.removeKey(lockKey);
        }
        return ResponseMapper.success(
                HttpStatus.CREATED.value(),
                "purchase amount debit successfully"
        );

    }

    public ApiResponse<UserAccountDetailsDto> getUserBalanceByAssetId(Long userid, Long AssetId) {
        UserAccountDetailsDto accounts = userAccountRepository.accountDetails(AssetId, userid)
                .orElseThrow(() -> new AccountNotFoundException("User account not Found"));
        return ResponseMapper.success(HttpStatus.OK.value(),
                "account balance fetch successfully",
                accounts);
    }

    public ApiResponse<List<UserAccountDetailsDto>> getUserBalance(Long userid) {
        List<UserAccountDetailsDto> accounts = userAccountRepository.allAccountDetails(userid);
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("User account not Found");
        }
        return ResponseMapper.success(HttpStatus.OK.value(),
                "account balance fetch successfully",
                accounts);
    }

    public ApiResponse<SystemAccountDetailsDto> getSystemBalanceById(Long id) {
        SystemAccountDetailsDto accounts = systemAccountRepository.accountDetails(id)
                .orElseThrow(() -> new AccountNotFoundException("System account not Found"));
        return ResponseMapper.success(HttpStatus.OK.value(),
                "account balance fetch successfully",
                accounts);
    }

}
