package com.example.wallet_service;

import com.example.wallet_service.dtos.request.BonusRequestDto;
import com.example.wallet_service.dtos.request.PurchaseRequestDto;
import com.example.wallet_service.dtos.request.TopupRequestDto;
import com.example.wallet_service.services.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

@SpringBootTest
class WalletServiceApplicationTests {


    private static final List<Long> userIds = LongStream.range(1, 11).boxed().toList();
    private static final List<Long> assetIds = LongStream.range(1, 4).boxed().toList();
    private static final long systemTreasuryAccount = 2;
    private static final long systemRevenueAccount = 1;

    private static final long userIdDefault=userIds.get(0);
    private static final long assetIdDefault=userIds.get(0);

    @Autowired
    private WalletService walletService;


    private String getIdempotencyKey() {
        return UUID.randomUUID().toString();
    }

    private Double getCurrentBalance(Long userId, Long assetId) {
        return walletService.getUserBalanceByAssetId(userId, assetId).getData().getAmount();
    }
    private Double getSystemRevenueAccountBalance() {
        return walletService.getSystemBalanceById(systemRevenueAccount).getData().getAmount();
    }
    private Double getSystemTreasuryAccountBalance() {
        return walletService.getSystemBalanceById(systemTreasuryAccount).getData().getAmount();
    }

    @Test
    void ValidTopupTest() { //here in every user every account amount add 100
        Double beforeSystemAccountBalance=getSystemRevenueAccountBalance();
        Double totalTopupAmount=0D;

        for (long userId : userIds) {
            for (long assetId : assetIds) {
                Double creditAmount = 100D;
                Double beforeCreditBalance = getCurrentBalance(userId, assetId);

                TopupRequestDto requestDto = TopupRequestDto.builder()
                        .userId(userId)
                        .assetId(assetId)
                        .amount(creditAmount)
                        .build();
                walletService.topup(requestDto, getIdempotencyKey());

                Double afterCreditBalance = getCurrentBalance(userId, assetId);
                assertEquals(beforeCreditBalance + creditAmount, afterCreditBalance);
                totalTopupAmount+=creditAmount;
            }
        }
        Double afterSystemAccountBalance=getSystemRevenueAccountBalance();
        assertEquals(beforeSystemAccountBalance + totalTopupAmount, afterSystemAccountBalance);

    }

    @Test
    void ValidBonusTest() { //here in every user every account amount add 100
        Double beforeSystemAccountBalance=getSystemTreasuryAccountBalance();
        Double totalBonusAmount =0D;

        for (long userId : userIds) {
            for (long assetId : assetIds) {
                Double bonusAmount = 10D;
                Double beforeBonusBalance = getCurrentBalance(userId, assetId);

                BonusRequestDto requestDto = BonusRequestDto.builder()
                        .userId(userId)
                        .assetId(assetId)
                        .amount(bonusAmount)
                        .build();
                walletService.bonus(requestDto, getIdempotencyKey());

                Double afterBonusBalance = getCurrentBalance(userId, assetId);
                assertEquals(beforeBonusBalance + bonusAmount, afterBonusBalance);
                totalBonusAmount += bonusAmount;
            }
        }
        Double afterSystemAccountBalance=getSystemTreasuryAccountBalance();
        assertEquals(beforeSystemAccountBalance - totalBonusAmount, afterSystemAccountBalance);

    }

    @Test
    void ValidPurchaseTest() { //here in every user every account amount add 100
        Double beforeSystemAccountBalance=getSystemRevenueAccountBalance();
        Double totalPurchaseAmount =0D;

        for (long userId : userIds) {
            for (long assetId : assetIds) {
                Double purchaseAmount = 10D;
                Double beforePurchaseBalance = getCurrentBalance(userId, assetId);

                PurchaseRequestDto requestDto = PurchaseRequestDto.builder()
                        .userId(userId)
                        .assetId(assetId)
                        .amount(purchaseAmount)
                        .build();
                walletService.purchase(requestDto, getIdempotencyKey());

                Double afterPurchaseBalance = getCurrentBalance(userId, assetId);
                assertEquals(beforePurchaseBalance - purchaseAmount, afterPurchaseBalance);
                totalPurchaseAmount += purchaseAmount;
            }
        }

        Double afterSystemAccountBalance=getSystemRevenueAccountBalance();
        assertEquals(beforeSystemAccountBalance + totalPurchaseAmount, afterSystemAccountBalance);

    }

    @Test
    void idempotencyTest(){
        String idempotencyKey=getIdempotencyKey();
        Double purchaseAmount = 10D;
        Double beforePurchaseBalance = getCurrentBalance(userIdDefault, assetIdDefault);

        PurchaseRequestDto requestDto = PurchaseRequestDto.builder()
                .userId(userIdDefault)
                .assetId(assetIdDefault)
                .amount(purchaseAmount)
                .build();

        walletService.purchase(requestDto,idempotencyKey);
        walletService.purchase(requestDto,idempotencyKey);

        Double afterPurchaseBalance = getCurrentBalance(userIdDefault, assetIdDefault);
        assertEquals(beforePurchaseBalance - purchaseAmount, afterPurchaseBalance);
    }


    @Test
    void concurrentPurchaseTest() throws InterruptedException {
        Double beforeSystemAccountBalance=getSystemRevenueAccountBalance();
        Double beforePurchaseBalance = getCurrentBalance(userIdDefault, assetIdDefault);
        Double purchaseAmount = 10D;
                int threadCount = 20; //here you will see you sent only 20 concurrent request but it only process 1
                ExecutorService executor = Executors.newFixedThreadPool(threadCount);
                CountDownLatch latch = new CountDownLatch(threadCount);
                int numberOfConcurrentOperation=1;
                for (int i = 0; i < threadCount; i++) {

                    executor.submit(() -> {
                        try {
                            PurchaseRequestDto requestDto = PurchaseRequestDto.builder()
                                    .userId(userIdDefault)
                                    .assetId(assetIdDefault)
                                    .amount(purchaseAmount)
                                    .build();

                            walletService.purchase(requestDto,getIdempotencyKey());
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        finally {

                            latch.countDown();
                        }
                    });
                }

                latch.await(); // Wait for all threads to finish
                executor.shutdown();
        Double afterPurchaseBalance = getCurrentBalance(userIdDefault, assetIdDefault);
        assertEquals(beforePurchaseBalance - purchaseAmount, afterPurchaseBalance);


        Double afterSystemAccountBalance=getSystemRevenueAccountBalance();
        assertEquals(beforeSystemAccountBalance + purchaseAmount, afterSystemAccountBalance);

    }

}
