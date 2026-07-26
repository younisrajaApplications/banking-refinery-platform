package com.younis.refinery.ingestion.dto;

public class RejectedTransactionResponse {

    private final long rowNumber;
    private final String transactionId;
    private final String rejectionReason;

    public RejectedTransactionResponse(
            long rowNumber,
            String transactionId,
            String rejectionReason) {
        this.rowNumber = rowNumber;
        this.transactionId = transactionId;
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public long getRowNumber() {
        return rowNumber;
    }
}
