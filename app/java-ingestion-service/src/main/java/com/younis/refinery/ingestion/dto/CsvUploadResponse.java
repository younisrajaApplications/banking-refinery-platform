package com.younis.refinery.ingestion.dto;

import java.util.List;

public class CsvUploadResponse {

    private final String fileName;
    private final long totalRows;
    private final long acceptedRows;
    private final long rejectedRows;
    private final List<RejectedRecordResponse> rejectedRecords;

    public CsvUploadResponse(String fileName,
                             long totalRows,
                             long acceptedRows,
                             long rejectedRows,
                             List<RejectedRecordResponse> rejectedRecords) {
        this.fileName = fileName;
        this.totalRows = totalRows;
        this.acceptedRows = acceptedRows;
        this.rejectedRows = rejectedRows;
        this.rejectedRecords = rejectedRecords;
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

    public List<RejectedRecordResponse> getRejectedRecords() {
        return rejectedRecords;
    }
}
