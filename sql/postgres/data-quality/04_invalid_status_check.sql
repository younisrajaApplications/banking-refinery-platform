-- Check: accepted transactions should only contain supported statuses
-- Expected result: zero rows
-- If rows are returned, unsupported statuses entered the accepted table.

SELECT
    ingestion_file_id,
    transaction_id,
    status,
    created_at
FROM accepted_transaction
WHERE status NOT IN ('APPROVED', 'DECLINED', 'PENDING', 'REVERSED');