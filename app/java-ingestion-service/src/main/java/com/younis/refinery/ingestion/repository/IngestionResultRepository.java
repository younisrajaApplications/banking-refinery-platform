package com.younis.refinery.ingestion.repository;

import com.younis.refinery.ingestion.dto.RejectedRecordResponse;
import com.younis.refinery.ingestion.dto.TransactionRequest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class IngestionResultRepository {

    private final JdbcClient jdbcClient;

    public IngestionResultRepository (JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional // This annotation means that either all the tables are updated or none are
    public long saveIngestionResult(
        String fileName,
        long totalRows,
        long acceptedRows,
        long rejectedRows,
        boolean reconciled,
        String processingStatus,
        String acceptedOutputPath,
        String rejectedOutputPath,
        String reconciliationReportPath,
        List<TransactionRequest> acceptedTransactions,
        List<RejectedRecordResponse> rejectedRecords
    ) {
        Long ingestionFileId = insertIngestionFile(
            fileName,
            totalRows,
            acceptedRows,
            rejectedRows,
            reconciled,
            processingStatus,
            acceptedOutputPath,
            rejectedOutputPath,
            reconciliationReportPath
        );

        for (TransactionRequest transaction : acceptedTransactions) {
            insertAcceptedTransaction(ingestionFileId, transaction);
        }

        for (RejectedRecordResponse rejectedRecord : rejectedRecords) {
            insertRejectedTransaction(ingestionFileId, rejectedRecord);
        }

        insertReconciliationReport(ingestionFileId, reconciliationReportPath);

        return ingestionFileId;
    }

    private Long insertIngestionFile(
        String fileName,
        long totalRows,
        long acceptedRows,
        long rejectedRows,
        boolean reconciled,
        String processingStatus,
        String acceptedOutputPath,
        String rejectedOutputPath,
        String reconciliationReportPath
    )
    {
        return jdbcClient.sql("""
            INSERT INTO ingestion_file (
                file_name,
                total_rows,
                accepted_rows,
                rejected_rows,
                reconciled,
                processing_status,
                accepted_output_path,
                rejected_output_path,
                reconciliation_report_path
            )
            VALUES (
                :fileName,
                :totalRows,
                :acceptedRows,
                :rejectedRows,
                :reconciled,
                :processingStatus,
                :acceptedOutputPath,
                :rejectedOutputPath,
                :reconciliationReportPath
            )
            RETURNING id
            """)
        .param("fileName", fileName)
        .param("totalRows", totalRows)
        .param("acceptedRows", acceptedRows)
        .param("rejectedRows", rejectedRows)
        .param("reconciled", reconciled)
        .param("processingStatus", processingStatus)
        .param("acceptedOutputPath", acceptedOutputPath)
        .param("rejectedOutputPath", rejectedOutputPath)
        .param("reconciliationReportPath", reconciliationReportPath)
        .query(Long.class)
        .single();
    }

    private void insertAcceptedTransaction(
        Long ingestionFileId,
        TransactionRequest transaction
    ) {
        jdbcClient.sql("""
            INSERT INTO accepted_transaction (
                ingestion_file_id,
                transaction_id,
                customer_id,
                account_id,
                transaction_timestamp,
                merchant_id,
                merchant_category,
                amount,
                currency,
                country,
                status,
                risk_score
            )
            VALUES (
                :ingestionFileId,
                :transactionId,
                :customerId,
                :accountId,
                :transactionTimestamp,
                :merchantId,
                :merchantCategory,
                :amount,
                :currency,
                :country,
                :status,
                :riskScore
            )
            """)
        .param("ingestionFileId", ingestionFileId)
        .param("transactionId", transaction.getTransactionId())
        .param("customerId", transaction.getCustomerId())
        .param("accountId", transaction.getAccountId())
        .param("transactionTimestamp", transaction.getTransactionTimestamp())
        .param("merchantId", transaction.getMerchantId())
        .param("merchantCategory", transaction.getMerchantCategory())
        .param("amount", transaction.getAmount())
        .param("currency", transaction.getCurrency())
        .param("country", transaction.getCountry())
        .param("status", transaction.getStatus())
        .param("riskScore", transaction.getRiskScore())
        .update();
    }

    private void insertRejectedTransaction(
            Long ingestionFileId,
            RejectedRecordResponse rejectedRecord
    ) {
        jdbcClient.sql("""
            INSERT INTO rejected_transaction (
                ingestion_file_id,
                row_number,
                transaction_id,
                rejection_reason
            )
            VALUES (
                :ingestionFileId,
                :rowNumber,
                :transactionId,
                :rejectionReason
            )
            """)
        .param("ingestionFileId", ingestionFileId)
        .param("rowNumber", rejectedRecord.getRowNumber())
        .param("transactionId", rejectedRecord.getTransactionId())
        .param("rejectionReason", rejectedRecord.getReason())
        .update();
    }

    private void insertReconciliationReport(
            Long ingestionFileId,
            String reconciliationReportPath
    ) {
        jdbcClient.sql("""
            INSERT INTO reconciliation_report (
                ingestion_file_id,
                report_path
            )
            VALUES (
                :ingestionFileId,
                :reportPath
            )
            """)
        .param("ingestionFileId", ingestionFileId)
        .param("reportPath", reconciliationReportPath)
        .update();
    }

}
