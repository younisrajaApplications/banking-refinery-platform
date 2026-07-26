SELECT
    i.id AS ingestion_file_id,
    i.total_rows,
    i.accepted_rows,
    CASE
        WHEN i.accepted_rows = 0 THEN 0
        ELSE ROUND(i.accepted_rows::NUMERIC/i.total_rows::NUMERIC, 2) * 100
    END AS accepted_percentage,
    i.rejected_rows,
    CASE
        WHEN i.rejected_rows = 0 THEN 0
        ELSE ROUND(i.rejected_rows::NUMERIC/i.total_rows::NUMERIC, 2) * 100
    END AS rejected_percentage,
    i.reconciled,
    i.processing_status,
    COUNT(r.ingestion_file_id) AS linked_reconciliation_reports,
    i.created_at
FROM ingestion_file i
LEFT OUTER JOIN reconciliation_report r ON i.id = r.ingestion_file_id
GROUP BY
    i.id,
    i.file_name,
    i.total_rows,
    i.accepted_rows,
    i.rejected_rows,
    i.reconciled,
    i.processing_status,
    i.created_at
ORDER BY i.created_at DESC;
