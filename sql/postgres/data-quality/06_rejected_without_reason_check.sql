-- Check: rejected transactions must always have a rejection reason
-- Expected result: zero rows
-- If rows are returned, rejected records are not explainable.

SELECT
    ingestion_file_id,
    row_number,
    transaction_id,
    rejection_reason,
    created_at
FROM rejected_transaction
WHERE rejection_reason IS NULL OR
      TRIM(rejection_reason) = '';