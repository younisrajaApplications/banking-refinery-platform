-- Check: ingestion row counts must reconcile
-- Expected result: zero rows
-- If rows are returned, accepted_rows + rejected_rows does not equal total_rows.

SELECT
    id AS ingestion_file_id,
    file_name,
    total_rows,
    accepted_rows,
    rejected_rows,
    accepted_rows + rejected_rows AS calculated_total,
    processing_status,
    created_at
FROM ingestion_file
WHERE total_rows <> accepted_rows + rejected_rows;