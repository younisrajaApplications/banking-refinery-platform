package com.younis.refinery.ingestion.dto;

public class TransactionValidationResponse {

    private boolean valid;
    private String message;
    private String transactionId;

    public TransactionValidationResponse(boolean valid, String message, String transactionId) {
        this.valid = valid;
        this.message = message;
        this.transactionId = transactionId;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public String getTransactionId() {
        return transactionId;
    }
}