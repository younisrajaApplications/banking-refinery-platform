package com.younis.refinery.ingestion.repository;

import com.younis.refinery.ingestion.dto.AcceptedTransactionResponse;
import com.younis.refinery.ingestion.dto.IngestionDetailResponse;
import com.younis.refinery.ingestion.dto.IngestionSummaryResponse;
import com.younis.refinery.ingestion.dto.RejectedTransactionResponse;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class IngestionQueryRepository {

    private final JdbcClient jdbcClient;

    public IngestionQueryRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<IngestionSummaryResponse> findRecentIngestions() {
        return jdbcClient.sql("""
            SELECT
                id,
                file_name,
                total_rows,
                accepted_rows,
                rejected_rows,
                reconciled,
                processing_status,
                created_at
            FROM ingestion_file
            ORDER BY created_at DESC
            LIMIT 20
            """)
            .query((rs, rowNum) -> new IngestionSummaryResponse(
                    rs.getLong("id"),
                    rs.getString("file_name"),
                    rs.getLong("total_rows"),
                    rs.getLong("accepted_rows"),
                    rs.getLong("rejected_rows"),
                    rs.getBoolean("reconciled"),
                    rs.getString("processing_status"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            ))
            .list();
    }

    public Optional<IngestionDetailResponse> findIngestionById(Long ingestionFileId) {
        Optional<IngestionDetailResponse> ingestion = jdbcClient.sql("""
            SELECT
                id,
                file_name,
                total_rows,
                accepted_rows,
                rejected_rows,
                reconciled,
                processing_status,
                accepted_output_path,
                rejected_output_path,
                reconciliation_report_path,
                created_at
            FROM ingestion_file
            WHERE id = :ingestionFileId
            """)
            .param("ingestionFileId", ingestionFileId)
            .query((rs, rowNum) -> new IngestionDetailResponse(
                    rs.getLong("id"),
                    rs.getString("file_name"),
                    rs.getLong("total_rows"),
                    rs.getLong("accepted_rows"),
                    rs.getLong("rejected_rows"),
                    rs.getBoolean("reconciled"),
                    rs.getString("processing_status"),
                    rs.getString("accepted_output_path"),
                    rs.getString("rejected_output_path"),
                    rs.getString("reconciliation_report_path"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    List.of(),
                    List.of()
            ))
            .optional();

        if (ingestion.isEmpty()) {
            return Optional.empty();
        }

        IngestionDetailResponse base = ingestion.get();

        List<AcceptedTransactionResponse> acceptedTransactions =
            findAcceptedTransactionsByIngestionId(ingestionFileId);

        List<RejectedTransactionResponse> rejectedTransactions =
            findRejectedTransactionsByIngestionId(ingestionFileId);

        return Optional.of(new IngestionDetailResponse(
            base.getIngestionFileId(),
            base.getFileName(),
            base.getTotalRows(),
            base.getAcceptedRows(),
            base.getRejectedRows(),
            base.isReconciled(),
            base.getProcessingStatus(),
            base.getAcceptedOutputPath(),
            base.getRejectedOutputPath(),
            base.getReconciliationReportPath(),
            base.getCreatedAt(),
            acceptedTransactions,
            rejectedTransactions
        ));
    }

    public List<RejectedTransactionResponse> findRejectedTransactionsByIngestionId(Long ingestionFileId) {
        return jdbcClient.sql("""
            SELECT
                row_number,
                transaction_id,
                rejection_reason
            FROM rejected_transaction
            WHERE ingestion_file_id = :ingestionFileId
            ORDER BY row_number
            """)
            .param("ingestionFileId", ingestionFileId)
            .query((rs, rowNum) -> new RejectedTransactionResponse(
                rs.getLong("row_number"),
                rs.getString("transaction_id"),
                rs.getString("rejection_reason")
            ))
            .list();
    }

    public List<AcceptedTransactionResponse> findAcceptedTransactionsByIngestionId(Long ingestionFileId) {
        return jdbcClient.sql("""
            SELECT
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
            FROM accepted_transaction
            WHERE ingestion_file_id = :ingestionFileId
            ORDER BY id
            """)
            .param("ingestionFileId", ingestionFileId)
            .query((rs, rowNum) -> new AcceptedTransactionResponse(
                rs.getString("transaction_id"),
                rs.getString("customer_id"),
                rs.getString("account_id"),
                rs.getTimestamp("transaction_timestamp").toLocalDateTime(),
                rs.getString("merchant_id"),
                rs.getString("merchant_category"),
                rs.getBigDecimal("amount"),
                rs.getString("currency"),
                rs.getString("country"),
                rs.getString("status"),
                rs.getObject("risk_score", Integer.class)
            ))
            .list();
    }

    public boolean existsById(Long ingestionFileId) {
        Integer count = jdbcClient.sql("""
            SELECT COUNT(*)
            FROM ingestion_file
            WHERE id = :ingestionFileId
            """)
            .param("ingestionFileId", ingestionFileId)
            .query(Integer.class)
            .single();
        return count > 0;
    }
}
