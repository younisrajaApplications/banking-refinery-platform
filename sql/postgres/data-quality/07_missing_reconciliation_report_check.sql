SELECT
    i.id AS ingestion_file_id,
    i.file_name,
    i.processing_status,
    i.created_at
FROM ingestion_file i
LEFT OUTER JOIN reconciliation_report r
ON i.id = r.ingestion_file_id
WHERE r.ingestion_file_id IS NULL;