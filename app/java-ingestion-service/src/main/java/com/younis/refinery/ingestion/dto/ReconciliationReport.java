package com.younis.refinery.ingestion.dto;

import java.util.Map;

public class ReconciliationReport {

    private final String fileName;
    private final long totalRows;
    private final long acceptedRows;
    private final long rejectedRows;
    private final boolean reconciled;
    private final String processingStatus;
    private final String acceptedOutputPath;
    private final String rejectedOutputPath;
    private final Map<String, Long> rejectedReasonSummary;
    private final String generatedAt;

    public ReconciliationReport(
            String fileName,
            long totalRows,
            long acceptedRows,
            long rejectedRows,
            boolean reconciled,
            String processingStatus,
            String acceptedOutputPath,
            String rejectedOutputPath,
            Map<String, Long> rejectedReasonSummary,
            String generatedAt) {
        this.fileName = fileName;
        this.totalRows = totalRows;
        this.acceptedRows = acceptedRows;
        this.rejectedRows = rejectedRows;
        this.reconciled = reconciled;
        this.processingStatus = processingStatus;
        this.acceptedOutputPath = acceptedOutputPath;
        this.rejectedOutputPath = rejectedOutputPath;
        this.rejectedReasonSummary = rejectedReasonSummary;
        this.generatedAt = generatedAt;
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

    public Map<String, Long> getRejectedReasonSummary() {
        return rejectedReasonSummary;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }
}
