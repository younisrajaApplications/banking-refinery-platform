package com.younis.refinery.ingestion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AcceptedTransactionResponse {

    private final String transactionId;
    private final String customerId;
    private final String accountId;
    private final LocalDateTime transactionTimestamp;
    private final String merchantId;
    private final String merchantCategory;
    private final BigDecimal amount;
    private final String currency;
    private final String country;
    private final String status;
    private final Integer riskScore;

    public AcceptedTransactionResponse(
            String transactionId,
            String customerId,
            String accountId,
            LocalDateTime transactionTimestamp,
            String merchantId,
            String merchantCategory,
            BigDecimal amount,
            String currency,
            String country,
            String status,
            Integer riskScore) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.accountId = accountId;
        this.transactionTimestamp = transactionTimestamp;
        this.merchantId = merchantId;
        this.merchantCategory = merchantCategory;
        this.amount = amount;
        this.currency = currency;
        this.country = country;
        this.status = status;
        this.riskScore = riskScore;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public LocalDateTime getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantCategory() {
        return merchantCategory;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCountry() {
        return country;
    }

    public String getStatus() {
        return status;
    }

    public Integer getRiskScore() {
        return riskScore;
    }
}
