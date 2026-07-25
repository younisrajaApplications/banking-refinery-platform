package com.younis.refinery.ingestion.dto;

import java.time.LocalDateTime;
import java.util.List;

public class IngestionDetailResponse {

    private final Long ingestionFileId;
    private final String fileName;
    private final long totalRows;
    private final long acceptedRows;
    private final long rejectedRows;
    private final boolean reconciled;
    private final String processingStatus;
    private final String acceptedOutputPath;
    private final String rejectedOutputPath;
    private final String reconciliationReportPath;
    private final LocalDateTime createdAt;
    private final List<AcceptedTransactionResponse> acceptedTransactions;
    private final List<RejectedTransactionResponse> rejectedTransactions;

    public IngestionDetailResponse(
            Long ingestionFileId,
            String fileName,
            long totalRows,
            long acceptedRows,
            long rejectedRows,
            boolean reconciled,
            String processingStatus,
            String acceptedOutputPath,
            String rejectedOutputPath,
            String reconciliationReportPath,
            LocalDateTime createdAt,
            List<AcceptedTransactionResponse> acceptedTransactions,
            List<RejectedTransactionResponse> rejectedTransactions) {
        this.ingestionFileId = ingestionFileId;
        this.fileName = fileName;
        this.totalRows = totalRows;
        this.acceptedRows = acceptedRows;
        this.rejectedRows = rejectedRows;
        this.reconciled = reconciled;
        this.processingStatus = processingStatus;
        this.acceptedOutputPath = acceptedOutputPath;
        this.rejectedOutputPath = rejectedOutputPath;
        this.reconciliationReportPath = reconciliationReportPath;
        this.createdAt = createdAt;
        this.acceptedTransactions = acceptedTransactions;
        this.rejectedTransactions = rejectedTransactions;
    }

    public Long getIngestionFileId() {
        return ingestionFileId;
    }

    public String getFileName() {
        return fileName;
    }

    public long getTotalRows() {
        return totalRows;
    }

    public long getAcceptedRows() {
        return acceptedRows;
    }

    public long getRejectedRows() {
        return rejectedRows;
    }

    public boolean isReconciled() {
        return reconciled;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public String getAcceptedOutputPath() {
        return acceptedOutputPath;
    }

    public String getRejectedOutputPath() {
        return rejectedOutputPath;
    }

    public String getReconciliationReportPath() {
        return reconciliationReportPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<AcceptedTransactionResponse> getAcceptedTransactions() {
        return acceptedTransactions;
    }

    public List<RejectedTransactionResponse> getRejectedTransactions() {
        return rejectedTransactions;
    }
}
