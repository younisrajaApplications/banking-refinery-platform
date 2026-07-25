package com.younis.refinery.ingestion.dto;

import java.time.LocalDateTime;

public class IngestionSummaryResponse {

    private final Long ingestionFileId;
    private final String fileName;
    private final long totalRows;
    private final long acceptedRows;
    private final long rejectedRows;
    private final boolean reconciled;
    private final String processingStatus;
    private final LocalDateTime createdAt;

    public IngestionSummaryResponse(
            Long ingestionFileId,
            String fileName,
            long totalRows,
            long acceptedRows,
            long rejectedRows,
            boolean reconciled,
            String processingStatus,
            LocalDateTime createdAt) {
        this.ingestionFileId = ingestionFileId;
        this.fileName = fileName;
        this.totalRows = totalRows;
        this.acceptedRows = acceptedRows;
        this.rejectedRows = rejectedRows;
        this.reconciled = reconciled;
        this.processingStatus = processingStatus;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
