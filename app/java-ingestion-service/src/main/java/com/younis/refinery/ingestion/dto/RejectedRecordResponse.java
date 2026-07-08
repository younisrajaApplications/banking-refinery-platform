package com.younis.refinery.ingestion.dto;

public class RejectedRecordResponse {

    private final long rowNumber;
    private final String transactionId;
    private final String reason;

    public RejectedRecordResponse(long rowNumber, String transactionId, String reason) {
        this.rowNumber = rowNumber;
        this.transactionId = transactionId;
        this.reason = reason;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public String getReason() {
        return reason;
    }
}
