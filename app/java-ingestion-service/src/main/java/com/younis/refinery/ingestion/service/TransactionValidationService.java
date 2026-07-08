package com.younis.refinery.ingestion.service;

import com.younis.refinery.ingestion.dto.TransactionRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class TransactionValidationService {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("GBP", "USD", "EUR");
    private static final Set<String> SUPPORTED_STATUSES = Set.of("APPROVED", "DECLINED", "PENDING", "REVERSED");

    public void validateBusinessRules(TransactionRequest request) {
        validateCurrency(request.getCurrency());
        validateStatus(request.getStatus());
        validateTransactionTimestamp(request.getTransactionTimestamp());
    }

    private void validateCurrency(String currency) {
        if (!SUPPORTED_CURRENCIES.contains(currency.toUpperCase())) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }

    private void validateStatus(String status) {
        if (!SUPPORTED_STATUSES.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Unsupported status: " + status);
        }
    }

    private void validateTransactionTimestamp(LocalDateTime transactionTimestamp) {
        if (transactionTimestamp.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("transactionTimestamp cannot be in the future");
        }
    }
}