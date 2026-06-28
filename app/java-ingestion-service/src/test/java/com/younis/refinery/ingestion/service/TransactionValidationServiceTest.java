package com.younis.refinery.ingestion.service;

import com.younis.refinery.ingestion.dto.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionValidationServiceTest {

    private TransactionValidationService transactionValidationService;

    @BeforeEach
    void setUp() {
        transactionValidationService = new TransactionValidationService();
    }

    private TransactionRequest validTransaction() {
        TransactionRequest request = new TransactionRequest();

        request.setTransactionId("TXN-1001");
        request.setCustomerId("CUST-001");
        request.setAccountId("ACC-001");
        request.setTransactionTimestamp(LocalDateTime.now().minusDays(1));
        request.setMerchantId("MERCH-001");
        request.setMerchantCategory("GROCERY");
        request.setAmount(new BigDecimal("42.50"));
        request.setCurrency("GBP");
        request.setCountry("GB");
        request.setStatus("APPROVED");
        request.setRiskScore(20);

        return request;
    }

    @Test
    void shouldPassValidationForValidTransaction() {
        TransactionRequest request = validTransaction();

        assertDoesNotThrow(() ->
                transactionValidationService.validateBusinessRules(request)
        );
    }

    @Test
    void shouldRejectUnsupportedCurrency() {
        TransactionRequest request = validTransaction();
        request.setCurrency("ABC");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionValidationService.validateBusinessRules(request)
        );

        assertEquals("Unsupported currency: ABC", exception.getMessage());
    }

    @Test
    void shouldRejectUnsupportedStatus() {
        TransactionRequest request = validTransaction();
        request.setStatus("FAILED");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionValidationService.validateBusinessRules(request)
        );

        assertEquals("Unsupported status: FAILED", exception.getMessage());
    }

    @Test
    void shouldRejectFutureTransactionTimestamp() {
        TransactionRequest request = validTransaction();
        request.setTransactionTimestamp(LocalDateTime.now().plusDays(1));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionValidationService.validateBusinessRules(request)
        );

        assertEquals("transactionTimestamp cannot be in the future", exception.getMessage());
    }

    @Test
    void shouldAcceptLowercaseSupportedCurrency() {
        TransactionRequest request = validTransaction();
        request.setCurrency("gbp");

        assertDoesNotThrow(() ->
                transactionValidationService.validateBusinessRules(request)
        );
    }

    @Test
    void shouldAcceptLowercaseSupportedStatus() {
        TransactionRequest request = validTransaction();
        request.setStatus("approved");

        assertDoesNotThrow(() ->
                transactionValidationService.validateBusinessRules(request)
        );
    }
}
